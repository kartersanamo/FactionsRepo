package com.kartersanamo.core.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;

final class CustomItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        CustomItem item = CustomItemIndex.from(event.getItem());
        if (item == null) {
            return;
        }
        event.setCancelled(true);
        item.handleUse(event.getPlayer());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        CustomItemIndex.remove(event.getPlugin());
    }
}
