package com.kartersanamo.core.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class CustomItem {

    private final String id;

    protected CustomItem(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Custom item id cannot be empty");
        }
        this.id = id;
    }

    public final String id() {
        return id;
    }

    public ItemStack create() {
        return NBTUtils.tag(appearance(), id);
    }

    public boolean matches(ItemStack item) {
        return NBTUtils.is(item, id);
    }

    public void give(Player player) {
        give(player, -1);
    }

    public void give(Player player, int slot) {
        if (player == null) {
            return;
        }
        ItemStack stack = create();
        PlayerInventory inventory = player.getInventory();
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, stack);
            return;
        }
        inventory.addItem(stack);
    }

    public boolean canMoveFromSlot() {
        return true;
    }

    public boolean canDrop() {
        return true;
    }

    public boolean canDropOnDeath() {
        return true;
    }

    public boolean canPickup() {
        return true;
    }

    public boolean canConsume() {
        return true;
    }

    public boolean canPlace() {
        return true;
    }

    public boolean canFill() {
        return true;
    }

    public boolean canCraft() {
        return true;
    }

    public boolean canEnchant() {
        return true;
    }

    public boolean canUseOnEntities() {
        return true;
    }

    public boolean canLaunch() {
        return true;
    }

    public boolean canFish() {
        return true;
    }

    protected abstract ItemStack appearance();

    protected void onUse(Player player) {
    }

    void handleUse(Player player) {
        onUse(player);
    }
}
