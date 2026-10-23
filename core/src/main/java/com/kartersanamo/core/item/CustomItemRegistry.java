package com.kartersanamo.core.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CustomItemRegistry {

    private final Plugin plugin;
    private final List<CustomItem> items = new ArrayList<>();

    public CustomItemRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public CustomItemRegistry register(CustomItem... toRegister) {
        for (CustomItem item : toRegister) {
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
}
