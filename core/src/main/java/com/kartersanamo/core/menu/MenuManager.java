package com.kartersanamo.core.menu;

import com.kartersanamo.core.menu.listeners.InventoryClickListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private static MenuManager instance;
    private final Plugin plugin;
    private final Map<String, Menu> guis;
    private final Map<UUID, Menu> openGuis;

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
        this.guis = new HashMap<>();
        this.openGuis = new HashMap<>();
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

    public void registerGUI(Menu gui) {
        if (gui != null) {
            guis.put(gui.getId(), gui);
        }
    }

    public Menu getGUI(String id) {
        return guis.get(id);
    }

    public void registerOpenGUI(Player player, Menu gui) {
        if (player != null && gui != null) {
            openGuis.put(player.getUniqueId(), gui);
        }
    }

    public void unregisterOpenGUI(Player player) {
        if (player != null) {
            openGuis.remove(player.getUniqueId());
        }
    }

    public Menu getOpenGUI(Player player) {
        if (player == null) {
            return null;
        }
        return openGuis.get(player.getUniqueId());
    }
}