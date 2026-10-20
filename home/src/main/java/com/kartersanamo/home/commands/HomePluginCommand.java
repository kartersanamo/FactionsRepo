package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.home.HomePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("homeplugin|h0m3")
public class HomePluginCommand extends BaseCommand {

    private final HomePlugin plugin;

    public HomePluginCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(CommandSender sender) {
        HomePlugin.messages().info(sender, "command-usage", "%usage%", "/homeplugin <reload|admin>");
    }

    @Subcommand("reload")
    @CommandPermission("home.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadPlugin();
        HomePlugin.messages().success(sender, "messages-reloaded");
    }

    @Subcommand("admin|manage")
    @CommandPermission("home.admin")
    @Syntax("[player]")
    public void onAdmin(Player player, @Optional String targetName) {
        AdminHomesCommand.openTargetHomes(player, plugin.getHomeManager(), targetName);
    }
}
