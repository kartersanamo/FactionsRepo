package com.kartersanamo.home.listeners;

import com.kartersanamo.home.managers.HomeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final HomeManager homeManager;

    public PlayerQuitListener(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        homeManager.unloadHomes(event.getPlayer().getUniqueId());
    }
}
