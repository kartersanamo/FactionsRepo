package com.kartersanamo.core;

import lombok.Getter;
import com.kartersanamo.core.menu.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CorePlugin extends JavaPlugin {

    @Getter
    public static CorePlugin instance;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        instance = this;
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
