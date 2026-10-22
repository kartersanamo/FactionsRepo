package com.kartersanamo.hub.listeners;

import com.kartersanamo.core.item.CustomItemRegistry;
import com.kartersanamo.hub.HubPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final CustomItemRegistry items;

    public PlayerJoinListener(CustomItemRegistry items) {
        this.items = items;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HubPlugin.messages().send(player, "welcome", "%player%", player.getName());

        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().clear();
        items.giveAll(player);
    }
}
