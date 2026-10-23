package com.kartersanamo.network.listeners;

import com.kartersanamo.network.NetworkPlugin;
import com.kartersanamo.network.managers.RebootManager;
import com.kartersanamo.network.managers.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class RebootListener implements Listener {

    private final NetworkPlugin plugin;
    private final RebootManager rebootManager;
    private final ServerManager serverManager;

    public RebootListener(NetworkPlugin plugin, RebootManager rebootManager, ServerManager serverManager) {
        this.plugin = plugin;
        this.rebootManager = rebootManager;
        this.serverManager = serverManager;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ServerInfo target = event.getTarget();
        String name = target.getName();
        if (!rebootManager.getRestarting().contains(name) || rebootManager.isHub(name)) {
            return;
        }
        ServerInfo hub = serverManager.getHubServer();
        if (hub == null) {
            return;
        }
        event.setTarget(hub);
        RebootManager.send(event.getPlayer(), plugin.message("server-unavailable", "%server%", name));
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String queuedServer = rebootManager.queuedServerOf(player.getUniqueId());
        if (queuedServer == null) {
            return;
        }
        if (rebootManager.getFailed().contains(queuedServer)) {
            RebootManager.send(player, plugin.message("failed", "%server%", queuedServer));
            rebootManager.dequeue(queuedServer, player.getUniqueId());
            rebootManager.saveQueues();
            return;
        }
        if (rebootManager.getRestarting().contains(queuedServer)) {
            RebootManager.send(player, plugin.message("queued-waiting", "%server%", queuedServer));
            return;
        }
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (player.isConnected()) {
                rebootManager.connectQueued(player, queuedServer);
            }
        }, 500, TimeUnit.MILLISECONDS);
    }
}
