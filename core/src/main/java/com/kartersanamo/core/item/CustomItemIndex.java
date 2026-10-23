package com.kartersanamo.core.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

final class CustomItemIndex {

    private static final Map<String, CustomItem> byId = new LinkedHashMap<>();
    private static final Map<Plugin, List<String>> byPlugin = new IdentityHashMap<>();

    private CustomItemIndex() {
    }

    static void add(Plugin plugin, CustomItem item) {
        String id = item.id();
        CustomItem existing = byId.get(id);
        if (existing != null && existing != item) {
            throw new IllegalStateException("Duplicate custom item id '" + id + "'");
        }
        byId.put(id, item);
        byPlugin.computeIfAbsent(plugin, _ -> new ArrayList<>()).add(id);
    }

    static void remove(Plugin plugin) {
        List<String> ids = byPlugin.remove(plugin);
        if (ids == null) {
            return;
        }
        for (String id : ids) {
            byId.remove(id);
        }
    }

    static CustomItem from(ItemStack stack) {
        String id = NBTUtils.get(stack);
        if (id == null) {
            return null;
        }
        return byId.get(id);
    }
}
