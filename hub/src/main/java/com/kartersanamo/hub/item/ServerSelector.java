package com.kartersanamo.hub.item;

import com.kartersanamo.core.item.CustomItem;
import com.kartersanamo.core.util.ItemBuilder;
import com.kartersanamo.hub.menu.ServerSelectorMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ServerSelector extends CustomItem {

    public ServerSelector() {
        super("server-selector");
    }

    @Override
    public int slot() {
        return 4;
    }

    @Override
    protected ItemStack appearance() {
        return new ItemBuilder(Material.COMPASS)
                .name("&a&lServer Selector")
                .lore("&7Click to choose a server")
                .glow()
                .build();
    }

    @Override
    public void onUse(Player player) {
        ServerSelectorMenu menu = new ServerSelectorMenu();
        menu.open(player);
    }
}
