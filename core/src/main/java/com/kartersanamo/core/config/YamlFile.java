package com.kartersanamo.core.config;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class YamlFile {

    private final Plugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration config;

    public YamlFile(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException exception) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create " + fileName, exception);
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        InputStream defaults = plugin.getResource(fileName);
        if (defaults != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8)
            );
            config.setDefaults(defaultConfig);
            config.options().copyDefaults(true);
            save();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + fileName, exception);
        }
    }

    public FileConfiguration raw() {
        return config;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public int getInt(String path, int def, int min, int max) {
        return Math.max(min, Math.min(max, getInt(path, def)));
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public float getFloat(String path, float def) {
        return (float) getDouble(path, def);
    }

    public List<String> getStringList(String path) {
        List<String> values = config.getStringList(path);
        return values == null ? Collections.emptyList() : values;
    }

    public int getGuiSize(String path, int fallback) {
        int size = getInt(path, fallback);
        if (size < 9 || size > 54 || size % 9 != 0) {
            return fallback;
        }
        return size;
    }

    public Material getMaterial(String path, Material def) {
        Material material = parseEnum(getString(path), Material.class);
        return material != null ? material : def;
    }

    public Sound getSound(String path) {
        return parseEnum(getString(path), Sound.class);
    }

    public Effect getEffect(String path) {
        return parseEnum(getString(path), Effect.class);
    }

    public <E extends Enum<E>> E getEnum(String path, Class<E> type) {
        return parseEnum(getString(path), type);
    }

    public <E extends Enum<E>> E getEnum(String path, Class<E> type, E def) {
        E value = parseEnum(getString(path), type);
        return value != null ? value : def;
    }

    private static <E extends Enum<E>> E parseEnum(String value, Class<E> type) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("none")) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
