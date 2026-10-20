package com.kartersanamo.logger;

import org.bukkit.plugin.java.JavaPlugin;

public class LoggerPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("LoggerPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LoggerPlugin has been disabled!");
    }
}
