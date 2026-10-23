package com.kartersanamo.network.managers;

import com.kartersanamo.network.NetworkPlugin;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;

@Getter
public class ServerManager {

    private final NetworkPlugin plugin;
    private final ServerInfo hubServer;

    public ServerManager(NetworkPlugin plugin) {
        this.plugin = plugin;
        this.hubServer = hubServer();
    }

    private ServerInfo hubServer() {
        String hubName = plugin.config().getString("hub-server", "hub");
        ServerInfo hub = plugin.getProxy().getServerInfo(hubName);
        if (hub == null) {
            plugin.getLogger().severe("Hub server " + hubName + " is not in BungeeCord config.");
        }
        return hub;
    }
}
