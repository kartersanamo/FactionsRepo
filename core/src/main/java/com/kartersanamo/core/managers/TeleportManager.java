package com.kartersanamo.core.managers;

import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.util.LocationUtil;
import com.kartersanamo.core.util.ParticleUtil;
import com.kartersanamo.core.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public TeleportManager(Plugin plugin, PluginConfig config, PluginMessages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    public void teleportToLocation(Player player, Location toLocation) {
        startTeleportCountdown(player, toLocation, null);
    }

    private void startTeleportCountdown(Player player, Location toLocation, Player target) {
        if (!config.getBoolean("teleport.delay.enabled", false)) {
            doTeleport(player, toLocation, target);
            return;
        }

        BukkitTask task = new BukkitRunnable() {
            int timeLeft = config.getInt("teleport.delay.seconds", 3);

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    doTeleport(player, toLocation, target);
                    cancel();
                    return;
                }
                messages.info(player, "teleport-countdown", "%seconds%", String.valueOf(timeLeft));
                if (config.getEffect("teleport.delay.particle") != null) {
                    ParticleUtil.spawnCircle(
                            player.getLocation(),
                            config.getDouble("teleport.delay.particle-radius", 1.0),
                            config.getEffect("teleport.delay.particle"),
                            config.getInt("teleport.delay.particle-count", 40)
                    );
                }
                if (config.getSound("teleport.delay.sound") != null) {
                    SoundUtil.play(
                            player,
                            config.getSound("teleport.delay.sound"),
                            config.getFloat("teleport.delay.sound-volume", 1.0f),
                            config.getFloat("teleport.delay.sound-pitch", 1.0f)
                    );
                }
                timeLeft -= 1;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        tasks.put(player.getUniqueId(), task);
    }

    private void doTeleport(Player player, Location toLocation, Player target) {
        player.teleport(toLocation);
        if (config.getSound("teleport.complete.sound") != null) {
            SoundUtil.play(
                    player,
                    config.getSound("teleport.complete.sound"),
                    config.getFloat("teleport.complete.sound-volume", 1.0f),
                    config.getFloat("teleport.complete.sound-pitch", 1.0f)
            );
        }
        if (config.getEffect("teleport.complete.particle") != null) {
            ParticleUtil.spawnCircle(
                    player.getLocation(),
                    config.getDouble("teleport.complete.particle-radius", 1.0),
                    config.getEffect("teleport.complete.particle"),
                    config.getInt("teleport.complete.particle-count", 40)
            );
        }
        if (target == null) {
            messages.info(player, "teleport-success-location", "%location%", LocationUtil.format(toLocation));
        } else {
            messages.info(player, "teleport-success-player", "%player%", target.getName());
        }
        removeTeleport(player.getUniqueId());
    }

    public void playerMoved(Player player) {
        BukkitTask task = tasks.get(player.getUniqueId());
        if (task == null) {
            return;
        }
        task.cancel();
        removeTeleport(player.getUniqueId());
        messages.error(player, "teleport-cancelled-moved");
    }

    public boolean isPlayerTeleporting(Player player) {
        return tasks.containsKey(player.getUniqueId());
    }

    private void removeTeleport(UUID uuid) {
        tasks.remove(uuid);
    }
}
