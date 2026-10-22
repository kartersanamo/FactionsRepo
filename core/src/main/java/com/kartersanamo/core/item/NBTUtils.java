package com.kartersanamo.core.item;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public final class NBTUtils {

    private static final String KEY = "ks_item";

    private NBTUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static ItemStack tag(ItemStack item, String id) {
        if (item == null || id == null || id.isEmpty()) {
            return item;
        }
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setString(KEY, id);
        nms.setTag(tag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static boolean is(ItemStack item, String id) {
        return id != null && id.equals(get(item));
    }

    public static String get(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.getTag();
        if (tag == null || !tag.hasKey(KEY)) {
            return null;
        }
        String id = tag.getString(KEY);
        return id.isEmpty() ? null : id;
    }
}
