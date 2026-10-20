package com.kartersanamo.core.util;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class EffectUtil {

    private EffectUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void play(
            Player player,
            Sound sound,
            float volume,
            float pitch,
            Effect effect,
            int particleCount,
            double particleRadius
    ) {
        if (player == null) {
            return;
        }
        if (sound != null) {
            SoundUtil.play(player, sound, volume, pitch);
        }
        if (effect != null && particleCount > 0 && particleRadius > 0) {
            ParticleUtil.spawnCircle(player.getLocation(), particleRadius, effect, particleCount);
        }
    }
}
