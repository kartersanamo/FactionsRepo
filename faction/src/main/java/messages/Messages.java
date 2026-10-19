package messages;

import lombok.Getter;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Loads and sends all player-facing messages from messages.yml.
 * Placeholders use {@code %name%} syntax. {@code %prefix%} is always available.
 */
public class Messages {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    @Getter
    private String prefix;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaults = plugin.getResource("messages.yml");
        if (defaults != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException exception) {
                plugin.getLogger().log(Level.WARNING, "Could not save messages.yml defaults", exception);
            }
        }

        prefix = colorize(config.getString("prefix", "&e[&6Factions&e]&r "));
    }

    public boolean hasList(String path) {
        return config.isList(path) && !config.getStringList(path).isEmpty();
    }

    public String raw(String path) {
        String value = config.getString(path);
        if (value == null) {
            plugin.getLogger().warning("Missing message: " + path);
            return colorize("&cMissing message: " + path);
        }
        return value;
    }

    public List<String> rawList(String path) {
        List<String> list = config.getStringList(path);
        if (list == null || list.isEmpty()) {
            String single = config.getString(path);
            if (single != null) {
                return Collections.singletonList(single);
            }
            plugin.getLogger().warning("Missing message list: " + path);
            return Collections.singletonList("&cMissing message: " + path);
        }
        return list;
    }

    public String format(String path, Object... placeholders) {
        return apply(raw(path), placeholders);
    }

    public List<String> formatList(String path, Object... placeholders) {
        List<String> formatted = new ArrayList<>();
        for (String line : rawList(path)) {
            formatted.add(apply(line, placeholders));
        }
        return formatted;
    }

    public void send(CommandSender sender, String path, Object... placeholders) {
        if (sender == null) {
            return;
        }
        if (config.isList(path)) {
            for (String line : formatList(path, placeholders)) {
                sender.sendMessage(line);
            }
            return;
        }
        sender.sendMessage(format(path, placeholders));
    }

    public void sendRaw(CommandSender sender, String message) {
        if (sender == null || message == null) {
            return;
        }
        sender.sendMessage(colorize(message.replace("%prefix%", prefix)));
    }

    public void sendBlank(CommandSender sender) {
        if (sender != null) {
            sender.sendMessage("");
        }
    }

    public void broadcast(String path, Object... placeholders) {
        String message = format(path, placeholders);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public void broadcastFaction(Faction faction, String path, Object... placeholders) {
        if (faction == null) {
            return;
        }
        String message = format(path, placeholders);
        for (FactionPlayer member : faction.getMembers()) {
            Player online = member.getPlayer();
            if (online != null) {
                online.sendMessage(message);
            }
        }
    }

    public void broadcastFactionRaw(Faction faction, String message) {
        if (faction == null || message == null) {
            return;
        }
        String colored = colorize(message.replace("%prefix%", prefix));
        for (FactionPlayer member : faction.getMembers()) {
            Player online = member.getPlayer();
            if (online != null) {
                online.sendMessage(colored);
            }
        }
    }

    public static String colorize(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private String apply(String template, Object... placeholders) {
        Map<String, String> map = toMap(placeholders);
        String result = template.replace("%prefix%", prefix);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return colorize(result);
    }

    private static Map<String, String> toMap(Object... placeholders) {
        Map<String, String> map = new HashMap<>();
        if (placeholders == null) {
            return map;
        }
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be key/value pairs");
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            Object key = placeholders[i];
            Object value = placeholders[i + 1];
            map.put(String.valueOf(key), value == null ? "" : String.valueOf(value));
        }
        return map;
    }
}
