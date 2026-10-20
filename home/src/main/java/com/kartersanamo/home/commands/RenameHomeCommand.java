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

@CommandAlias("renamehome|changename")
public class RenameHomeCommand extends BaseCommand {

    private final HomeManager homeManager;

    public RenameHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name> <new_name>")
    @CommandCompletion("@homes")
    public void onRenameHome(Player player, String homeName, String newName) {
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        if (home == null) {
            HomePlugin.messages().error(player, "home-not-found-name", "%name%", homeName);
            return;
        }
        if (homeManager.homeNameHasColor(newName)) {
            HomePlugin.messages().error(player, "home-name-color-codes");
            return;
        }
        if (homeManager.exists(player.getUniqueId(), newName)) {
            HomePlugin.messages().error(player, "home-name-exists");
            return;
        }
        if (homeManager.isHomeNameCorrectSize(newName)) {
            HomePlugin.messages().error(
                    player,
                    "home-name-size",
                    "%min%", String.valueOf(HomePlugin.config().getInt("home.name.min-length", 3)),
                    "%max%", String.valueOf(HomePlugin.config().getInt("home.name.max-length", 16))
            );
            return;
        }

        home.setDisplayName(newName);
        homeManager.update(home);
        HomePlugin.messages().info(player, "home-name-changed", "%old%", homeName, "%new%", newName);
        HomePlugin.config().playEffect(player, "effects.rename-home");
    }
}
