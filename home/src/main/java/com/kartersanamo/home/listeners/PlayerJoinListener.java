package com.kartersanamo.home.listeners;

import com.kartersanamo.home.managers.HomeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final HomeManager homeManager;

    public PlayerJoinListener(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        homeManager.loadHomes(event.getPlayer().getUniqueId());
    }
}
