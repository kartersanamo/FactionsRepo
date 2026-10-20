package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import org.bukkit.entity.Player;

@CommandAlias("deletehome|delhome|dh")
public class DeleteHomeCommand extends BaseCommand {

    private final HomeManager homeManager;

    public DeleteHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name>")
    @CommandCompletion("@homes")
    public void onDeleteHome(Player player, String homeName) {
        if (!homeManager.exists(player.getUniqueId(), homeName)) {
            HomePlugin.messages().error(player, "home-not-found-name", "%name%", homeName);
            return;
        }
        homeManager.deleteHome(player.getUniqueId(), homeName);
        HomePlugin.messages().info(player, "home-deleted", "%name%", homeName);
        HomePlugin.config().playEffect(player, "effects.delete-home");
    }
}
