package com.kartersanamo.core.managers;

import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {

    private final Plugin plugin;
    private final PluginMessages messages;
    private final Map<UUID, ChatInputSession> activeSessions;

    public ChatInputManager(Plugin plugin, PluginMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
        this.activeSessions = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void awaitInput(Player player, String prompt, Consumer<String> handler, Runnable cancelHandler) {
        UUID uuid = player.getUniqueId();
        player.sendMessage(ColorUtil.translate(prompt));

        String keyword = messages.getOr("chatinput-cancel-keyword", "cancel");
        player.sendMessage(messages.get("chatinput-cancel-hint", "%keyword%", keyword));
        activeSessions.put(uuid, new ChatInputSession(handler, cancelHandler));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatInputSession session = activeSessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage().trim();
        String keyword = messages.getOr("chatinput-cancel-keyword", "cancel");

        if (message.equalsIgnoreCase(keyword)) {
            activeSessions.remove(player.getUniqueId());
            if (session.cancelHandler() != null) {
                plugin.getServer().getScheduler().runTask(plugin, session.cancelHandler());
            }
            return;
        }

        activeSessions.remove(player.getUniqueId());
        plugin.getServer().getScheduler().runTask(plugin, () -> session.handler().accept(message));
    }

    public void cancelInput(Player player) {
        activeSessions.remove(player.getUniqueId());
    }

    public boolean isWaitingForInput(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    private record ChatInputSession(Consumer<String> handler, Runnable cancelHandler) {
    }
}
