package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.core.util.LocationUtil;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("movehome")
public class MoveHomeCommand extends BaseCommand {

    private final HomeManager homeManager;

    public MoveHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name>")
    @CommandCompletion("@homes")
    public void onMoveHome(Player player, String homeName) {
        if (!homeManager.exists(player.getUniqueId(), homeName)) {
            HomePlugin.messages().error(player, "home-not-found-name", "%name%", homeName);
            return;
        }

        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        Location oldLocation = home.getLocation();
        Location newLocation = player.getLocation();
        home.setLocation(newLocation);
        homeManager.update(home);
        HomePlugin.messages().info(
                player,
                "home-location-updated",
                "%old%", LocationUtil.format(oldLocation),
                "%new%", LocationUtil.format(newLocation)
        );
        HomePlugin.config().playEffect(player, "effects.move-home");
    }
}
