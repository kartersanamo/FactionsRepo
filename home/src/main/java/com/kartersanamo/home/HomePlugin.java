package com.kartersanamo.home;

import org.bukkit.plugin.java.JavaPlugin;

public class HomePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("HomePlugin has been enabled!");
        // Register commands and events here
    }

    @Override
    public void onDisable() {
        getLogger().info("HomePlugin has been disabled!");
        // Cleanup resources here
    }
}