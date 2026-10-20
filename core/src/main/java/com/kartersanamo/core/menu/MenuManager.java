package com.kartersanamo.core.menu;

import com.kartersanamo.core.menu.listeners.InventoryClickListener;
import com.kartersanamo.core.menu.listeners.InventoryCloseListener;
import com.kartersanamo.core.menu.listeners.InventoryDragListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private static MenuManager instance;
    private final Plugin plugin;
    private final Map<String, Menu> menus;
    private final Map<UUID, Menu> openMenus;

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
        this.menus = new HashMap<>();
        this.openMenus = new HashMap<>();
        registerListeners();
    }


    public static MenuManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new MenuManager(plugin);
        }
        return instance;
    }


    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new InventoryClickListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InventoryDragListener(), plugin);
    }

    public void registerMenu(Menu menu) {
        if (menu != null) {
            menus.put(menu.getId(), menu);
        }
    }


    public Menu getMenu(String id) {
        return menus.get(id);
    }

    public void registerOpenMenu(Player player, Menu menu) {
        if (player != null && menu != null) {
            openMenus.put(player.getUniqueId(), menu);
        }
    }

    public void unregisterOpenMenu(Player player) {
        if (player != null) {
            openMenus.remove(player.getUniqueId());
        }
    }

    public Menu getOpenMenu(Player player) {
        if (player == null) {
            return null;
        }
        return openMenus.get(player.getUniqueId());
    }
}