package com.kartersanamo.home.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.menus.AdminMenu;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("adminhomes|ahomes")
@CommandPermission("home.admin")
public class AdminHomesCommand extends BaseCommand {

    private final HomeManager homeManager;

    public AdminHomesCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Default
    @Syntax("[player]")
    @CommandCompletion("@players")
    public void onAdminHomes(Player player, @Optional String targetName) {
        openTargetHomes(player, homeManager, targetName);
    }

    public static void openTargetHomes(Player admin, HomeManager homeManager, String targetName) {
        if (targetName == null || targetName.isBlank()) {
            new AdminMenu(homeManager, admin).open(admin);
            return;
        }

        UUID targetId = homeManager.resolvePlayerUuid(targetName);
        if (targetId == null) {
            HomePlugin.messages().error(admin, "player-not-found", "%player%", targetName);
            return;
        }
        new AdminMenu(homeManager, admin, targetId).open(admin);
    }
}
