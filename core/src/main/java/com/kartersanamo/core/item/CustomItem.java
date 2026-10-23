package com.kartersanamo.core.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class CustomItem implements Listener {

    private final String id;

    protected CustomItem(String id) {
        this.id = id;
    }

    public final String id() {
        return id;
    }

    public int slot() {
        return -1;
    }

    public ItemStack create() {
        return NBTUtils.tag(appearance(), id);
    }

    public boolean matches(ItemStack item) {
        return NBTUtils.is(item, id);
    }

    public void give(Player player) {
        if (player == null) {
            return;
        }
        ItemStack item = create();
        PlayerInventory inventory = player.getInventory();
        int slot = slot();
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
            return;
        }
        inventory.addItem(item);
    }

    protected abstract ItemStack appearance();

    protected void onUse(Player player) {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (!matches(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        onUse(event.getPlayer());
    }
}
