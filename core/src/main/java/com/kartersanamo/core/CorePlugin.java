package com.kartersanamo.core;

import lombok.Getter;
import com.kartersanamo.core.menu.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CorePlugin extends JavaPlugin {

    public static CorePlugin plugin;
    private MenuManager menuManager;

    public static CorePlugin getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.menuManager = MenuManager.getInstance(this);
        getLogger().info("CorePlugin has been enabled!");
        // Register commands and events here
    }

    @Override
    public void onDisable() {
        getLogger().info("CorePlugin has been disabled!");
        // Cleanup resources here
    }
}
