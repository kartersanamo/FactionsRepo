package com.kartersanamo.hub;

import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.item.CustomItems;
import com.kartersanamo.hub.item.HubItems;
import com.kartersanamo.hub.listeners.PlayerJoinListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class HubPlugin extends JavaPlugin {

    @Getter
    private static HubPlugin instance;
    private PluginConfig pluginConfig;
    private PluginMessages pluginMessages;

    public static PluginConfig config() {
        return instance.pluginConfig;
    }

    public static PluginMessages messages() {
        return instance.pluginMessages;
    }

    @Override
    public void onEnable() {
        instance = this;
        pluginConfig = new PluginConfig(this);
        pluginMessages = new PluginMessages(this);
        CustomItems.register(this, HubItems.class);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getLogger().info("Hub has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Hub has been disabled!");
    }
}
