package com.kartersanamo.core.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

final class CustomItemRestrictionListener implements Listener {

    private static boolean isDropClick(InventoryClickEvent event) {
        ClickType type = event.getClick();
        return type == ClickType.DROP
                || type == ClickType.CONTROL_DROP
                || type == ClickType.WINDOW_BORDER_LEFT
                || type == ClickType.WINDOW_BORDER_RIGHT;
    }

    private static void cancel(Cancellable event, ItemStack stack, Predicate<CustomItem> allowed) {
        if (denied(stack, allowed)) {
            event.setCancelled(true);
        }
    }

    private static boolean denied(ItemStack[] stacks, Predicate<CustomItem> allowed) {
        for (ItemStack stack : stacks) {
            if (denied(stack, allowed)) {
                return true;
            }
        }
        return false;
    }

    private static boolean denied(ItemStack stack, Predicate<CustomItem> allowed) {
        CustomItem item = CustomItemIndex.from(stack);
        return item != null && !allowed.test(item);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        cancel(event, event.getItemDrop().getItemStack(), CustomItem::canDrop);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack[] stacks = {
                event.getCurrentItem(),
                event.getCursor(),
                event.getHotbarButton() >= 0
                        ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton())
                        : null
        };
        if (denied(stacks, CustomItem::canMoveFromSlot)) {
            event.setCancelled(true);
            return;
        }
        if (isDropClick(event) && denied(stacks, CustomItem::canDrop)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (denied(event.getOldCursor(), CustomItem::canMoveFromSlot)) {
            event.setCancelled(true);
            return;
        }
        for (ItemStack item : event.getNewItems().values()) {
            if (denied(item, CustomItem::canMoveFromSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(stack -> denied(stack, CustomItem::canDropOnDeath));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        cancel(event, event.getItem().getItemStack(), CustomItem::canPickup);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        cancel(event, event.getItem(), CustomItem::canConsume);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        cancel(event, event.getItemInHand(), CustomItem::canPlace);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        cancel(event, event.getPlayer().getItemInHand(), CustomItem::canPlace);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        cancel(event, event.getPlayer().getItemInHand(), CustomItem::canFill);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory crafting)) {
            return;
        }
        if (denied(crafting.getResult(), CustomItem::canCraft) || denied(crafting.getMatrix(), CustomItem::canCraft)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        cancel(event, event.getItem(), CustomItem::canEnchant);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        cancel(event, event.getPlayer().getItemInHand(), CustomItem::canUseOnEntities);
    }

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent event) {
        cancel(event, event.getPlayerItem(), CustomItem::canUseOnEntities);
        if (!event.isCancelled()) {
            cancel(event, event.getArmorStandItem(), CustomItem::canUseOnEntities);
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        cancel(event, event.getPlayer().getItemInHand(), CustomItem::canUseOnEntities);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            cancel(event, player.getItemInHand(), CustomItem::canLaunch);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            cancel(event, event.getBow(), CustomItem::canLaunch);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        cancel(event, event.getPlayer().getItemInHand(), CustomItem::canFish);
    }
}
