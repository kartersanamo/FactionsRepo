package com.kartersanamo.hub.listeners;

import com.kartersanamo.hub.HubPlugin;
import com.kartersanamo.hub.item.HubItems;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HubPlugin.messages().sendList(player, "welcome-messages", "%player%", player.getName());

        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().clear();
        HubItems.SERVER_SELECTOR.give(player, 4);
    }
}
