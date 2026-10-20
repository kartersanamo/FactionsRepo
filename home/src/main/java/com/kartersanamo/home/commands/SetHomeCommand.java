package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.core.util.LocationUtil;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

@CommandAlias("sethome")
public class SetHomeCommand extends BaseCommand {

    private final HomeManager homeManager;

    public SetHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("<name>")
    public void onSetHome(Player player, String name) {
        Location location = player.getLocation();
        if (homeManager.isHomeNameCorrectSize(name)) {
            HomePlugin.messages().error(
                    player,
                    "home-name-size",
                    "%min%", String.valueOf(HomePlugin.config().getInt("home.name.min-length", 3)),
                    "%max%", String.valueOf(HomePlugin.config().getInt("home.name.max-length", 16))
            );
            return;
        }
        if (homeManager.homeNameHasColor(name)) {
            HomePlugin.messages().error(player, "home-name-color-codes");
            return;
        }

        int maxHomes = HomePlugin.config().getInt("home.max-number", 10);
        if (homeManager.getHomeCount(player.getUniqueId()) >= maxHomes && !homeManager.exists(player.getUniqueId(), name)) {
            HomePlugin.messages().error(player, "home-limit-reached", "%max%", String.valueOf(maxHomes));
            return;
        }

        if (homeManager.exists(player.getUniqueId(), name)) {
            Home home = homeManager.getHome(player.getUniqueId(), name);
            home.setLocation(location);
            homeManager.update(home);
        } else {
            Material material = HomePlugin.config().getMaterial("home.default-material", Material.CHEST);
            Home home = new Home(
                    "home_" + player.getUniqueId() + "_" + UUID.randomUUID(),
                    player,
                    name,
                    new ArrayList<>(),
                    material,
                    location,
                    System.currentTimeMillis(),
                    0
            );
            homeManager.addHome(player.getUniqueId(), home);
        }

        HomePlugin.config().playEffect(player, "effects.set-home");
        HomePlugin.messages().info(
                player,
                "home-set",
                "%name%", name,
                "%location%", LocationUtil.format(location)
        );
    }
}
