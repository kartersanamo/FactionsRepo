package com.kartersanamo.hub.menu;

import com.kartersanamo.core.menu.Menu;
import com.kartersanamo.core.util.ColorUtil;
import com.kartersanamo.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ServerSelectorMenu extends Menu {

    public ServerSelectorMenu() {
        super(
                "server-selector-menu",
                "Server Selector",
                9);
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
        setClickHandler(0, this::handleClick);
        setClickHandler(1, this::handleClick);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        HumanEntity clicker = event.getWhoClicked();
        if (clicker instanceof Player player) {
            player.closeInventory();
            player.sendMessage(ColorUtil.translate("&aConnecting to the server..."));
            // Add the logic to connect the player to the selected server here.
        } else {
            clicker.closeInventory();
            clicker.sendMessage(ColorUtil.translate("&cWho tf are you?"));
        }
    }
}
