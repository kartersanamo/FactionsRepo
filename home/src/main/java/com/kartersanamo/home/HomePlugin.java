package com.kartersanamo.home;

import co.aikar.commands.BukkitCommandManager;
import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.listeners.TeleportMoveListener;
import com.kartersanamo.core.managers.ChatInputManager;
import com.kartersanamo.core.managers.TeleportManager;
import com.kartersanamo.home.commands.AdminHomesCommand;
import com.kartersanamo.home.commands.DeleteHomeCommand;
import com.kartersanamo.home.commands.EditHomeCommand;
import com.kartersanamo.home.commands.HomeCommand;
import com.kartersanamo.home.commands.HomeInfoCommand;
import com.kartersanamo.home.commands.HomePluginCommand;
import com.kartersanamo.home.commands.HomesCommand;
import com.kartersanamo.home.commands.MoveHomeCommand;
import com.kartersanamo.home.commands.RenameHomeCommand;
import com.kartersanamo.home.commands.SetHomeCommand;
import com.kartersanamo.home.listeners.PlayerJoinListener;
import com.kartersanamo.home.listeners.PlayerQuitListener;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@Getter
public class HomePlugin extends JavaPlugin {

    private static HomePlugin plugin;
    private PluginConfig pluginConfig;
    private PluginMessages pluginMessages;
    private HomeManager homeManager;
    private ChatInputManager chatInputManager;
    private TeleportManager teleportManager;

    public static HomePlugin getInstance() {
        return plugin;
    }

    public static PluginConfig config() {
        return plugin.pluginConfig;
    }

    public static PluginMessages messages() {
        return plugin.pluginMessages;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    @Override
    public void onEnable() {
        plugin = this;
        pluginConfig = new PluginConfig(this);
        pluginMessages = new PluginMessages(this);
        homeManager = new HomeManager(this);
        chatInputManager = new ChatInputManager(this, pluginMessages);
        teleportManager = new TeleportManager(this, pluginConfig, pluginMessages);

        registerCommands();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(homeManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(homeManager), this);
        getServer().getPluginManager().registerEvents(new TeleportMoveListener(teleportManager), this);

        for (Player player : getServer().getOnlinePlayers()) {
            homeManager.loadHomes(player.getUniqueId());
        }

        getLogger().info("HomePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                homeManager.unloadHomes(player.getUniqueId());
            }
        }
        getLogger().info("HomePlugin has been disabled!");
    }

    public void reloadPlugin() {
        pluginConfig.reload();
        pluginMessages.reload();
    }

    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("homes", context -> {
            Player player = context.getPlayer();
            if (player == null) {
                return java.util.List.of();
            }
            Map<String, Home> homes = homeManager.getHomes(player.getUniqueId());
            if (homes == null || homes.isEmpty()) {
                return java.util.List.of();
            }
            return homes.values().stream().map(Home::getDisplayName).toList();
        });
        commandManager.registerCommand(new SetHomeCommand(homeManager));
        commandManager.registerCommand(new HomeCommand(homeManager));
        commandManager.registerCommand(new DeleteHomeCommand(homeManager));
        commandManager.registerCommand(new HomesCommand(homeManager));
        commandManager.registerCommand(new RenameHomeCommand(homeManager));
        commandManager.registerCommand(new MoveHomeCommand(homeManager));
        commandManager.registerCommand(new EditHomeCommand(homeManager));
        commandManager.registerCommand(new HomeInfoCommand(homeManager));
        commandManager.registerCommand(new AdminHomesCommand(homeManager));
        commandManager.registerCommand(new HomePluginCommand(this));
    }
}
