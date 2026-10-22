package com.kartersanamo.core;

import com.kartersanamo.core.menu.MenuManager;
import com.kartersanamo.core.util.BungeeUtil;
import lombok.Getter;
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
        BungeeUtil.register(this);
        getLogger().info("CorePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CorePlugin has been disabled!");
        // Cleanup resources here
    }
}
