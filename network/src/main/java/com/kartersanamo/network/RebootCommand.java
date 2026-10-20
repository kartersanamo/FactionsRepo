package com.kartersanamo.network;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

public class RebootCommand extends Command {

    private final NetworkPlugin plugin;
    private final RebootManager rebootManager;

    public RebootCommand(NetworkPlugin plugin, RebootManager rebootManager) {
        super("reboot", null, "rb");
        this.plugin = plugin;
        this.rebootManager = rebootManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.config().getString("permission", "network.reboot"))) {
            send(sender, plugin.message("no-permission"));
            return;
        }
        String serverName;
        if (sender instanceof ProxiedPlayer player) {
            if (args.length != 1) {
                send(sender, plugin.message("usage"));
                return;
            }
            Server server = player.getServer();
            if (server == null) {
                send(sender, plugin.message("unknown-server"));
                return;
            }
            serverName = server.getInfo().getName();
        } else if (args.length == 2) {
            serverName = args[1];
        } else {
            send(sender, plugin.message("console-usage"));
            return;
        }
        if (!rebootManager.isRebootable(serverName)) {
            send(sender, plugin.message("server-not-allowed"));
            return;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            rebootManager.cancel(sender, serverName);
            return;
        }

        try {
            int seconds = rebootManager.parseDurationSeconds(args[0]);
            rebootManager.schedule(sender, serverName, seconds);
        } catch (IllegalArgumentException exception) {
            send(sender, plugin.message(
                    "invalid-duration",
                    "%min%", formatLimit(plugin.config().getInt("min-duration-seconds", 1)),
                    "%max%", formatLimit(plugin.config().getInt("max-duration-seconds", 21600))
            ));
        }
    }

    private static void send(CommandSender sender, String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    private static String formatLimit(int seconds) {
        if (seconds >= 3600 && seconds % 3600 == 0) {
            return (seconds / 3600) + "h";
        }
        if (seconds >= 60 && seconds % 60 == 0) {
            return (seconds / 60) + "m";
        }
        return seconds + "s";
    }
}
