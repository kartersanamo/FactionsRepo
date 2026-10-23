package com.kartersanamo.hub.item;

import com.kartersanamo.core.item.CustomItem;
import com.kartersanamo.core.util.ItemBuilder;
import com.kartersanamo.core.util.ServerBrandingUtil;
import com.kartersanamo.hub.menu.ServerSelectorMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ServerSelector extends CustomItem {

    public ServerSelector() {
        super("server-selector");
    }

    @Override
    public boolean canMoveFromSlot() {
        return false;
    }

    @Override
    public boolean canDrop() {
        return false;
    }

    @Override
    public boolean canDropOnDeath() {
        return false;
    }

    @Override
    public boolean canPickup() {
        return false;
    }

    @Override
    public boolean canCraft() {
        return false;
    }

    @Override
    public boolean canUseOnEntities() {
        return false;
    }

    @Override
    protected ItemStack appearance() {
        return new ItemBuilder(Material.COMPASS)
                .name(ServerBrandingUtil.getPrimaryColor() + "&lServer Selector")
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
