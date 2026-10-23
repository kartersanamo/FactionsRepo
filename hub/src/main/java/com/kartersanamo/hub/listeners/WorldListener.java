package com.kartersanamo.hub.listeners;

import com.kartersanamo.hub.HubPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        HubPlugin.getInstance().correctWeather(event.getWorld());
    }
}
