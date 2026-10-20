package com.kartersanamo.core.menu.listeners;

import com.kartersanamo.core.CorePlugin;
import com.kartersanamo.core.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Menu menu = CorePlugin.getInstance().getMenuManager().getOpenMenu(player);

        if (menu != null) {
            event.setCancelled(true);
            menu.handleClick(event);
        }
    }
}
