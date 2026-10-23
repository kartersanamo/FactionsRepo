package com.kartersanamo.hub;

import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.item.CustomItems;
import com.kartersanamo.hub.item.HubItems;
import com.kartersanamo.hub.listeners.PlayerJoinListener;
import com.kartersanamo.hub.listeners.PlayerListener;
import com.kartersanamo.hub.listeners.WorldListener;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class HubPlugin extends JavaPlugin {

    private static HubPlugin plugin;
    private PluginConfig pluginConfig;
    private PluginMessages pluginMessages;

    public static HubPlugin getInstance() {
        return plugin;
    }

    public static PluginConfig config() {
        return plugin.pluginConfig;
    }

    public static PluginMessages messages() {
        return plugin.pluginMessages;
    }

    @Override
    public void onEnable() {
        plugin = this;
        pluginConfig = new PluginConfig(this);
        pluginMessages = new PluginMessages(this);
        CustomItems.register(this, HubItems.class);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        getServer().getWorlds().forEach(this::correctWeather);

        getLogger().info("Hub has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Hub has been disabled!");
    }

    public void correctWeather(World world) {
        world.setStorm(false);
        world.setThundering(false);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(0L);
    }
}
