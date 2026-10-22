package com.kartersanamo.hub.menu;

import com.kartersanamo.core.menu.Menu;
import com.kartersanamo.core.util.BungeeUtil;
import com.kartersanamo.core.util.ItemBuilder;
import com.kartersanamo.hub.HubPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ServerSelectorMenu extends Menu {

    public ServerSelectorMenu() {
        super("server-selector-menu", "Server Selector", 9);
        build();
    }

    private void build() {
        ItemStack devServer1 = new ItemBuilder(Material.COMPASS)
                .name("&aDev Server #1")
                .lore("&7Click to join the Dev Server #1")
                .build();
        ItemStack devServer2 = new ItemBuilder(Material.COMPASS)
                .name("&aDev Server #2")
                .lore("&7Click to join the Dev Server #2")
                .build();
        setItem(0, devServer1);
        setItem(1, devServer2);
        setClickHandler(0, event -> connectPlayer(event, "dev-1"));
        setClickHandler(1, event -> connectPlayer(event, "dev-2"));
    }

    private void connectPlayer(InventoryClickEvent event, String server) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            event.getWhoClicked().closeInventory();
            return;
        }
        player.closeInventory();
        HubPlugin.messages().send(player, "connecting", "%server%", server);
        if (!BungeeUtil.connect(player, server)) {
            HubPlugin.messages().error(player, "connect-failed", "%server%", server);
        }
    }
}
