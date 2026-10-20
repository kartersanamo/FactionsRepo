package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import org.bukkit.entity.Player;

@CommandAlias("home")
public class HomeCommand extends BaseCommand {

    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name>")
    @CommandCompletion("@homes")
    public void onHome(Player player, String name) {
        if (homeManager.homeNameHasColor(name)) {
            HomePlugin.messages().error(player, "home-name-color-codes");
            return;
        }
        if (!homeManager.exists(player.getUniqueId(), name)) {
            HomePlugin.messages().error(player, "home-not-found-name", "%name%", name);
            return;
        }

        Home home = homeManager.getHome(player.getUniqueId(), name);
        if (home == null) {
            HomePlugin.messages().error(player, "home-get-failed", "%name%", name);
            return;
        }
        home.teleport();
        homeManager.update(home);
    }
}
