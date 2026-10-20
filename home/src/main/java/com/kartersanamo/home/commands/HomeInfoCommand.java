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

@CommandAlias("homeinfo")
public class HomeInfoCommand extends BaseCommand {

    private final HomeManager homeManager;

    public HomeInfoCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name>")
    @CommandCompletion("@homes")
    public void onHomeInfo(Player player, String homeName) {
        if (!homeManager.exists(player.getUniqueId(), homeName)) {
            HomePlugin.messages().error(player, "home-not-found-name", "%name%", homeName);
            return;
        }
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        homeManager.sendInfo(home, player);
    }
}
