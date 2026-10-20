package com.kartersanamo.core.menu.listeners;

import com.kartersanamo.core.CorePlugin;
import com.kartersanamo.core.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Menu menu = CorePlugin.getInstance().getMenuManager().getOpenMenu(player);

        if (menu != null) {
            menu.handleClose(event);
            CorePlugin.getInstance().getMenuManager().unregisterOpenMenu(player);
        }
    }
}
