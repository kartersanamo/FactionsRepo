package com.kartersanamo.network.commands;

import com.kartersanamo.network.managers.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    private final ServerManager serverManager;

    public HubCommand(ServerManager serverManager) {
        super("hub", null, "lobby");
        this.serverManager = serverManager;
    }

    @Override
    public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            ServerInfo hubServer = serverManager.getHubServer();
            if (hubServer == null) {
                player.sendMessage(ChatColor.RED + "The Hub Server could not be found. Please try again later");
                return;
            }
            player.sendMessage(ChatColor.GREEN + "Connecting to the Hub Server...");
            player.connect(hubServer);
        }
    }
}
