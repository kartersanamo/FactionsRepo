package com.kartersanamo.faction.commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class HomeSubCommand extends FactionSubCommand {
    public HomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("home")
    public void onHome(Player player) {
        FactionResult result = factionSystem.teleportHome(player);
        switch (result) {
            case SUCCESS:
                msg(player, "home.teleport");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_HOME:
                msg(player, "home.teleport-failed");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
