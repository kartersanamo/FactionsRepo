package com.kartersanamo.core.config;

import com.kartersanamo.core.util.ColorUtil;
import com.kartersanamo.core.util.PlaceholderUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public final class PluginMessages {

    private final Plugin plugin;
    private final YamlFile yaml;

    public PluginMessages(Plugin plugin) {
        this(plugin, "messages.yml");
    }

    public PluginMessages(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.yaml = new YamlFile(plugin, fileName);
    }

    public void reload() {
        yaml.reload();
    }

    public YamlFile yaml() {
        return yaml;
    }

    public String raw(String path) {
        String value = yaml.getString(path);
        if (value == null) {
            plugin.getLogger().warning("Missing message: " + path);
            return "&cMissing message: " + path;
        }
        return value;
    }

    public String getOr(String path, String def) {
        String value = yaml.getString(path);
        return value != null ? value : def;
    }

    public String get(String path, String... placeholders) {
        return ColorUtil.translate(PlaceholderUtil.replace(raw(path), placeholders));
    }

    public List<String> getList(String path, String... placeholders) {
        List<String> values = yaml.getStringList(path);
        if (values.isEmpty()) {
            String single = yaml.getString(path);
            if (single != null) {
                return List.of(get(path, placeholders));
            }
            plugin.getLogger().warning("Missing message list: " + path);
            return List.of(ColorUtil.translate("&cMissing message: " + path));
        }

        List<String> formatted = new ArrayList<>(values.size());
        for (String line : values) {
            formatted.add(ColorUtil.translate(PlaceholderUtil.replace(line, placeholders)));
        }
        return formatted;
    }

    public void send(CommandSender sender, String path, String... placeholders) {
        if (sender != null) {
            sender.sendMessage(get(path, placeholders));
        }
    }

    public void error(CommandSender sender, String path, String... placeholders) {
        sendPrefixed(sender, "chat-prefix-default", "&c", path, placeholders);
    }

    public void info(CommandSender sender, String path, String... placeholders) {
        sendPrefixed(sender, "chat-prefix-default", "&7", path, placeholders);
    }

    public void success(CommandSender sender, String path, String... placeholders) {
        sendPrefixed(sender, "chat-prefix-success", "&a", path, placeholders);
    }

    public void warning(CommandSender sender, String path, String... placeholders) {
        sendPrefixed(sender, "chat-prefix-warning", "&e", path, placeholders);
    }

    private void sendPrefixed(
            CommandSender sender,
            String prefixPath,
            String color,
            String path,
            String... placeholders
    ) {
        if (sender == null) {
            return;
        }
        String prefix = ColorUtil.translate(getOr(prefixPath, ""));
        String body = ColorUtil.translate(color + PlaceholderUtil.replace(raw(path), placeholders));
        if (prefix.isEmpty()) {
            sender.sendMessage(body);
            return;
        }
        sender.sendMessage(prefix + " " + body);
    }
}
