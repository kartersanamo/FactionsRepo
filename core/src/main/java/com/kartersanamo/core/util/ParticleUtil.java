package com.kartersanamo.core.util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ParticleUtil {

    private ParticleUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void spawn(Location location, Effect effect, int count) {
        if (location == null || location.getWorld() == null || effect == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            location.getWorld().playEffect(location, effect, 0);
        }
    }

    public static void spawn(Player player, Location location, Effect effect, int count) {
        if (player == null || location == null || location.getWorld() == null || effect == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            location.getWorld().playEffect(location, effect, 0);
        }
    }

    public static void spawnCircle(Location center, double radius, Effect effect, int count) {
        if (center == null || center.getWorld() == null || effect == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            spawn(loc, effect, 1);
        }
    }
}
