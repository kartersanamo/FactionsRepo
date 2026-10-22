package com.kartersanamo.core.util;

import com.kartersanamo.core.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public final class BungeeUtil {

    public static final String CHANNEL = "BungeeCord";

    private BungeeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void register(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    public static boolean connect(Player player, String server) {
        return connect(CorePlugin.getInstance(), player, server);
    }

    public static boolean connect(Plugin plugin, Player player, String server) {
        if (plugin == null || player == null || server == null || server.isEmpty()) {
            return false;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException exception) {
            plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to connect " + player.getName() + " to " + server,
                    exception
            );
            return false;
        }
        player.sendPluginMessage(plugin, CHANNEL, bytes.toByteArray());
        return true;
    }
}
