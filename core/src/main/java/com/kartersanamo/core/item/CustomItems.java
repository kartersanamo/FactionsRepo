package com.kartersanamo.core.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface CustomItems {

    static void install(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CustomItemListener(), plugin);
    }

    static <E extends Enum<E> & CustomItems> void register(Plugin plugin, Class<E> type) {
        for (E value : type.getEnumConstants()) {
            CustomItemIndex.add(plugin, value.item());
        }
    }

    static CustomItem from(ItemStack stack) {
        return CustomItemIndex.from(stack);
    }

    CustomItem item();

    default ItemStack create() {
        return item().create();
    }

    default void give(Player player) {
        item().give(player);
    }

    default void give(Player player, int slot) {
        item().give(player, slot);
    }

    @SuppressWarnings("unchecked")
    default <T extends CustomItem> T get() {
        return (T) item();
    }
}
