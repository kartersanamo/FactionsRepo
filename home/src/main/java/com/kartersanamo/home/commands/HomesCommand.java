package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.menus.HomesMenu;
import org.bukkit.entity.Player;

@CommandAlias("homes")
public class HomesCommand extends BaseCommand {

    private final HomeManager homeManager;

    public HomesCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    public void onHomes(Player player) {
        new HomesMenu(homeManager, player).open(player);
    }
}
