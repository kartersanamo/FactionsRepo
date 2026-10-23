package com.kartersanamo.network.managers;

import com.kartersanamo.network.MinecraftStatus;
import com.kartersanamo.network.NetworkPlugin;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class RebootManager {

    private static final Pattern DURATION =
            Pattern.compile("^(\\d+)([smh])?$", Pattern.CASE_INSENSITIVE);
    private final NetworkPlugin plugin;
    private final Map<String, List<ScheduledTask>> scheduled = new ConcurrentHashMap<>();
    private final Set<String> restarting = ConcurrentHashMap.newKeySet();
    private final Set<String> failed = ConcurrentHashMap.newKeySet();
    private final Map<String, LinkedHashSet<UUID>> queues = new ConcurrentHashMap<>();

    public RebootManager(NetworkPlugin plugin) {
        this.plugin = plugin;
        loadQueues();
    }

    public boolean isRebootable(String serverName) {
        return composeService(serverName) != null;
    }


    public int parseDurationSeconds(String raw) {
        Matcher matcher = DURATION.matcher(raw.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("invalid duration");
        }
        long value = Long.parseLong(matcher.group(1));
        if (value <= 0) {
            throw new IllegalArgumentException("invalid duration");
        }
        String unit = matcher.group(2);
        long multiplier = switch (unit == null ? "s" : unit.toLowerCase()) {
            case "s" -> 1L;
            case "m" -> 60L;
            case "h" -> 3600L;
            default -> throw new IllegalArgumentException("invalid duration");
        };
        if (value > Integer.MAX_VALUE / multiplier) {
            throw new IllegalArgumentException("invalid duration");
        }
        int seconds = (int) (value * multiplier);
        int min = plugin.config().getInt("min-duration-seconds", 1);
        int max = plugin.config().getInt("max-duration-seconds", 21600);
        if (seconds < min || seconds > max) {
            throw new IllegalArgumentException("invalid duration");
        }
        return seconds;
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    public void cancel(CommandSender sender, String serverName) {
        if (restarting.contains(serverName)) {
            send(sender, plugin.message("too-late-to-cancel", "%server%", serverName));
            return;
        }
        List<ScheduledTask> tasks = scheduled.remove(serverName);
        if (tasks == null) {
            send(sender, plugin.message("nothing-to-cancel", "%server%", serverName));
            return;
        }
        cancelTasks(tasks);
        plugin.getLogger().info("Cancelled reboot of " + serverName + " by " + sender.getName());
        broadcast(serverName, plugin.message("cancelled", "%server%", serverName));
    }

    public void startSchedulePolling() {
        plugin.getProxy().getScheduler().schedule(plugin, this::pollScheduleRequests, 1, 1, TimeUnit.SECONDS);
    }

    public void recoverQueues() {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            for (String serverName : new ArrayList<>(queues.keySet())) {
                if (queue(serverName).isEmpty() || restarting.contains(serverName) || failed.contains(serverName)) {
                    continue;
                }
                plugin.getLogger().info("Checking leftover queue for " + serverName);
                waitUntilReady(serverName, false);
            }
        });
    }

    public void shutdown() {
        scheduled.values().forEach(RebootManager::cancelTasks);
        scheduled.clear();
        saveQueues();
    }

    private void startSecondCountdown(String serverName, int seconds) {
        if (!scheduled.containsKey(serverName) || restarting.contains(serverName)) {
            return;
        }
        for (int remaining = seconds; remaining >= 1; remaining--) {
            int shown = remaining;
            ScheduledTask task = plugin.getProxy().getScheduler().schedule(
                    plugin,
                    () -> broadcast(serverName, plugin.message(
                            "countdown",
                            "%server%", serverName,
                            "%seconds%", Integer.toString(shown))),
                    seconds - remaining,
                    TimeUnit.SECONDS);
            scheduled.computeIfPresent(serverName, (name, tasks) -> {
                tasks.add(task);
                return tasks;
            });
        }
    }

    public void schedule(CommandSender sender, String serverName, int seconds) {
        if (restarting.contains(serverName)) {
            send(sender, plugin.message("restart-in-progress", "%server%", serverName));
            return;
        }
        failed.remove(serverName);
        if (scheduled.containsKey(serverName)) {
            send(sender, plugin.message("already-scheduled", "%server%", serverName));
            return;
        }
        if (plugin.getServerManager().getHubServer() == null) {
            send(sender, plugin.message(
                    "hub-missing",
                    "%hub%", plugin.config().getString("hub-server", "hub")));
            return;
        }

        List<ScheduledTask> tasks = new CopyOnWriteArrayList<>();
        tasks.add(plugin.getProxy().getScheduler().schedule(
                plugin, () -> beginRestart(serverName), seconds, TimeUnit.SECONDS));

        if (seconds > 60) {
            tasks.add(plugin.getProxy().getScheduler().schedule(
                    plugin,
                    () -> broadcast(serverName, plugin.message("one-minute", "%server%", serverName)),
                    seconds - 60,
                    TimeUnit.SECONDS));
        }

        int countdownLength = Math.min(10, seconds);
        tasks.add(plugin.getProxy().getScheduler().schedule(
                plugin,
                () -> startSecondCountdown(serverName, countdownLength),
                seconds - countdownLength,
                TimeUnit.SECONDS));

        scheduled.put(serverName, tasks);
        plugin.getLogger().info("Scheduled reboot of " + serverName + " in " + seconds + "s by " + sender.getName());
        broadcast(serverName, plugin.message(
                "scheduled",
                "%server%", serverName,
                "%time%", formatDuration(seconds)));
    }

    private void waitUntilReady(String serverName, boolean requireDowntime) {
        ServerInfo backend = plugin.getProxy().getServerInfo(serverName);
        if (backend == null) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> failQueue(serverName), 0, TimeUnit.MILLISECONDS);
            return;
        }

        long pollMillis = Math.max(200L, plugin.config().getInt("ready-poll-millis", 500));
        int neededPings = Math.max(1, plugin.config().getInt("ready-success-pings", 2));
        long downDeadline = System.currentTimeMillis()
                + TimeUnit.SECONDS.toMillis(plugin.config().getInt("down-timeout-seconds", 90));
        long readyDeadline = System.currentTimeMillis()
                + TimeUnit.SECONDS.toMillis(plugin.config().getInt("ready-timeout-seconds", 180));

        boolean sawDown = !requireDowntime;
        if (requireDowntime) {
            while (System.currentTimeMillis() < downDeadline) {
                if (!MinecraftStatus.isReady(backend.getAddress())) {
                    sawDown = true;
                    plugin.getLogger().info(serverName + " is down; waiting for Minecraft to accept connections.");
                    break;
                }
                sleep(pollMillis);
            }
            if (!sawDown) {
                plugin.getLogger().severe(serverName + " never went offline. Is the host restart watcher running?");
                plugin.getProxy().getScheduler().schedule(plugin, () -> failQueue(serverName), 0, TimeUnit.MILLISECONDS);
                return;
            }
        }

        int successes = 0;
        while (System.currentTimeMillis() < readyDeadline) {
            if (MinecraftStatus.isReady(backend.getAddress())) {
                successes++;
                if (successes >= neededPings) {
                    plugin.getLogger().info(serverName + " is ready for Minecraft connections.");
                    plugin.getProxy().getScheduler().schedule(
                            plugin, () -> finishRestart(serverName), 0, TimeUnit.MILLISECONDS);
                    return;
                }
            } else {
                successes = 0;
            }
            sleep(pollMillis);
        }

        plugin.getLogger().severe(serverName + " did not become ready before timeout.");
        plugin.getProxy().getScheduler().schedule(plugin, () -> failQueue(serverName), 0, TimeUnit.MILLISECONDS);
    }

    private void finishRestart(String serverName) {
        restarting.remove(serverName);
        LinkedHashSet<UUID> waiting = new LinkedHashSet<>(queue(serverName));
        for (UUID uuid : waiting) {
            ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
            if (player != null && player.isConnected()) {
                connectQueued(player, serverName);
            }
        }
        plugin.getLogger().info("Processed return queue for " + serverName);
    }

    private void failQueue(String serverName) {
        restarting.remove(serverName);
        scheduled.remove(serverName);
        failed.add(serverName);
        String message = plugin.message("failed", "%server%", serverName);
        for (UUID uuid : new ArrayList<>(queue(serverName))) {
            ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
            if (player != null && player.isConnected()) {
                send(player, message);
                dequeue(serverName, uuid);
            }
        }
        saveQueues();
        plugin.getLogger().warning("Restart failed for " + serverName + "; remaining queue kept for offline players.");
    }

    private void beginRestart(String serverName) {
        List<ScheduledTask> tasks = scheduled.remove(serverName);
        cancelTasks(tasks);
        if (!restarting.add(serverName)) {
            return;
        }

        ServerInfo backend = plugin.getProxy().getServerInfo(serverName);
        ServerInfo hub = plugin.getServerManager().getHubServer();
        if (backend == null || hub == null) {
            restarting.remove(serverName);
            plugin.getLogger().severe("Cannot restart " + serverName + ": hub or backend is missing.");
            return;
        }

        enqueuePlayers(backend);
        saveQueues();
        String moveMessage = plugin.message("restarting", "%server%", serverName);
        for (ProxiedPlayer player : new ArrayList<>(backend.getPlayers())) {
            send(player, moveMessage);
            player.connect(hub);
        }

        if (!writeRestartRequest(serverName)) {
            restarting.remove(serverName);
            failQueue(serverName);
            return;
        }

        plugin.getLogger().info("Restart requested for " + serverName
                + " (" + queue(serverName).size() + " queued players)");
        plugin.getProxy().getScheduler().runAsync(plugin, () -> waitUntilReady(serverName, true));
    }

    private void enqueuePlayers(ServerInfo backend) {
        LinkedHashSet<UUID> queue = queue(backend.getName());
        for (ProxiedPlayer player : backend.getPlayers()) {
            queue.add(player.getUniqueId());
        }
    }

    public void connectQueued(ProxiedPlayer player, String serverName) {
        ServerInfo backend = plugin.getProxy().getServerInfo(serverName);
        if (backend == null || restarting.contains(serverName)) {
            return;
        }
        send(player, plugin.message("returned", "%server%", serverName));
        player.connect(backend, (success, error) -> {
            if (Boolean.TRUE.equals(success)) {
                dequeue(serverName, player.getUniqueId());
                saveQueues();
                return;
            }
            send(player, plugin.message("failed", "%server%", serverName));
            plugin.getLogger().warning("Could not return " + player.getName() + " to " + serverName
                    + (error != null ? ": " + error.getMessage() : ""));
        });
    }

    private LinkedHashSet<UUID> queue(String serverName) {
        return queues.computeIfAbsent(serverName, key -> new LinkedHashSet<>());
    }

    public void dequeue(String serverName, UUID uuid) {
        LinkedHashSet<UUID> queue = queues.get(serverName);
        if (queue == null) {
            return;
        }
        queue.remove(uuid);
        if (queue.isEmpty()) {
            queues.remove(serverName);
        }
    }

    private boolean writeRestartRequest(String serverName) {
        String service = composeService(serverName);
        if (service == null || !service.matches("^[a-zA-Z0-9][a-zA-Z0-9_-]*$")) {
            plugin.getLogger().severe("Refusing restart; " + serverName + " is not allowlisted.");
            return false;
        }
        File directory = new File(plugin.getDataFolder(), plugin.config().getString("request-dir", "restart-requests"));
        if (!directory.exists() && !directory.mkdirs()) {
            plugin.getLogger().severe("Could not create " + directory);
            return false;
        }
        File request = new File(directory, service + ".request");
        String body = "service=" + service + System.lineSeparator()
                + "requestedAt=" + Instant.now() + System.lineSeparator();
        try {
            Files.writeString(request.toPath(), body, StandardCharsets.UTF_8);
            plugin.getLogger().info("Wrote restart request " + request.getName());
            return true;
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not write restart request for " + service, exception);
            return false;
        }
    }

    private String composeService(String serverName) {
        Configuration allowed = plugin.config().getSection("allowed-servers");
        if (allowed == null) {
            return null;
        }
        String service = allowed.getString(serverName);
        if (service == null || service.isBlank()) {
            return null;
        }
        return service;
    }

    public String queuedServerOf(UUID uuid) {
        for (Map.Entry<String, LinkedHashSet<UUID>> entry : queues.entrySet()) {
            if (entry.getValue().contains(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void pollScheduleRequests() {
        File dir = new File(plugin.getDataFolder(), "schedule-requests");
        File[] files = dir.listFiles(File::isFile);
        if (files == null) {
            return;
        }
        for (File file : files) {
            String serverName = file.getName();
            if (serverName.endsWith(".request")) {
                serverName = serverName.substring(0, serverName.length() - ".request".length());
            }
            String raw;
            try {
                raw = Files.readString(file.toPath()).trim();
            } catch (IOException exception) {
                plugin.getLogger().log(Level.WARNING, "Could not read " + file.getName(), exception);
                continue;
            }
            if (!file.delete()) {
                plugin.getLogger().warning("Could not delete schedule request " + file.getName());
                continue;
            }
            if (!isRebootable(serverName)) {
                plugin.getLogger().warning("Ignoring schedule request for " + serverName);
                continue;
            }
            if (raw.isEmpty()) {
                raw = "1";
            }
            try {
                int seconds = parseDurationSeconds(raw);
                plugin.getLogger().info("Schedule request for " + serverName + " in " + seconds + "s");
                schedule(plugin.getProxy().getConsole(), serverName, seconds);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Invalid duration in " + file.getName() + ": " + raw);
            }
        }
    }

    private void broadcast(String serverName, String message) {
        ServerInfo backend = plugin.getProxy().getServerInfo(serverName);
        if (backend == null) {
            return;
        }
        for (ProxiedPlayer player : backend.getPlayers()) {
            send(player, message);
        }
    }

    private void loadQueues() {
        File file = queueFile();
        if (!file.exists()) {
            return;
        }
        try {
            Configuration yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            Configuration section = yaml.getSection("queues");
            if (section == null) {
                return;
            }
            for (String serverName : section.getKeys()) {
                LinkedHashSet<UUID> ids = new LinkedHashSet<>();
                for (String raw : section.getStringList(serverName)) {
                    try {
                        ids.add(UUID.fromString(raw));
                    } catch (IllegalArgumentException ignored) {
                        plugin.getLogger().warning("Ignoring invalid queued UUID " + raw);
                    }
                }
                if (!ids.isEmpty()) {
                    queues.put(serverName, ids);
                }
            }
            plugin.getLogger().info("Loaded reboot queues for " + queues.keySet());
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not load reboot queues", exception);
        }
    }

    public boolean isHub(String serverName) {
        return plugin.config().getString("hub-server", "hub").equalsIgnoreCase(serverName);
    }

    private File queueFile() {
        return new File(plugin.getDataFolder(), plugin.config().getString("queue-file", "queues.yml"));
    }

    private static void cancelTasks(List<ScheduledTask> tasks) {
        if (tasks == null) {
            return;
        }
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
    }

    public void saveQueues() {
        Configuration yaml = new Configuration();
        for (Map.Entry<String, LinkedHashSet<UUID>> entry : queues.entrySet()) {
            List<String> ids = new ArrayList<>();
            for (UUID uuid : entry.getValue()) {
                ids.add(uuid.toString());
            }
            yaml.set("queues." + entry.getKey(), ids);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(yaml, queueFile());
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save reboot queues", exception);
        }
    }

    private static String formatDuration(int seconds) {
        if (seconds >= 3600 && seconds % 3600 == 0) {
            int hours = seconds / 3600;
            return hours + (hours == 1 ? " hour" : " hours");
        }
        if (seconds >= 60 && seconds % 60 == 0) {
            int minutes = seconds / 60;
            return minutes + (minutes == 1 ? " minute" : " minutes");
        }
        return seconds + (seconds == 1 ? " second" : " seconds");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

}
