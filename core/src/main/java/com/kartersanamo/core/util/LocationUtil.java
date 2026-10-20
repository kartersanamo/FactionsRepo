package com.kartersanamo.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public final class LocationUtil {

    private LocationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String format(Location location) {
        if (location == null) {
            return "null";
        }
        return String.format("%s: %.2f, %.2f, %.2f",
                Objects.requireNonNull(location.getWorld()).getName(),
                location.getX(),
                location.getY(),
                location.getZ());
    }

    public static Location fromConfig(ConfigurationSection section, String path) {
        if (section == null) {
            return null;
        }
        Object value = section.get(path);
        if (value instanceof Location location) {
            return location;
        }
        ConfigurationSection locSection = section.getConfigurationSection(path);
        if (locSection == null) {
            return null;
        }
        String worldName = locSection.getString("world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(
                world,
                locSection.getDouble("x"),
                locSection.getDouble("y"),
                locSection.getDouble("z"),
                (float) locSection.getDouble("yaw"),
                (float) locSection.getDouble("pitch")
        );
    }
}