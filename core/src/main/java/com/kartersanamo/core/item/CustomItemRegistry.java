package com.kartersanamo.core.item;

import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CustomItemRegistry {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final String configRoot;
    private final List<CustomItem> items = new ArrayList<>();

    public CustomItemRegistry(Plugin plugin, PluginConfig config, PluginMessages messages) {
        this(plugin, config, messages, "items");
    }

    public CustomItemRegistry(
            Plugin plugin,
            PluginConfig config,
            PluginMessages messages,
            String configRoot
    ) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.configRoot = configRoot;
    }

    public CustomItemRegistry register(CustomItem... toRegister) {
        for (CustomItem item : toRegister) {
            item.bind(this);
            plugin.getServer().getPluginManager().registerEvents(item, plugin);
            items.add(item);
        }
        return this;
    }

    public void giveAll(Player player) {
        for (CustomItem item : items) {
            item.give(player);
        }
    }

    public CustomItem from(ItemStack stack) {
        String id = NBTUtils.get(stack);
        if (id == null) {
            return null;
        }
        for (CustomItem item : items) {
            if (item.id().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public List<CustomItem> items() {
        return Collections.unmodifiableList(items);
    }

    PluginConfig config() {
        return config;
    }

    PluginMessages messages() {
        return messages;
    }

    String configRoot() {
        return configRoot;
    }
}
