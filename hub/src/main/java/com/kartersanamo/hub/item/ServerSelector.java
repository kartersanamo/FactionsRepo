package com.kartersanamo.hub.item;

import com.kartersanamo.core.item.CustomItem;
import com.kartersanamo.hub.menu.ServerSelectorMenu;
import org.bukkit.entity.Player;

public final class ServerSelector extends CustomItem {

    public ServerSelector() {
        super("server-selector");
    }

    @Override
    public void onUse(Player player) {
        ServerSelectorMenu menu = new ServerSelectorMenu();
        menu.open(player);
    }
}
