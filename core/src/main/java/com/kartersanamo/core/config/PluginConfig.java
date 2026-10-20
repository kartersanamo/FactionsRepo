package com.kartersanamo.core.config;

import com.kartersanamo.core.util.EffectUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class PluginConfig extends YamlFile {

    public PluginConfig(Plugin plugin) {
        super(plugin, "config.yml");
    }

    public PluginConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public void playEffect(Player player, String path) {
        EffectUtil.play(
                player,
                getSound(path + ".sound"),
                getFloat(path + ".sound-volume", 1.0f),
                getFloat(path + ".sound-pitch", 1.0f),
                getEffect(path + ".particle"),
                getInt(path + ".particle-count", 0),
                getDouble(path + ".particle-radius", 0.0)
        );
    }
}
