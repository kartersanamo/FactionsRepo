package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class PowerboostSubCommand extends FactionSubCommand {
    public PowerboostSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("powerboost")
    @CommandPermission("factions.admin")
    public void onPowerboost(Player player, String factionName, int boost) {
        FactionResult result = factionSystem.setPowerBoost(factionSystem.getFactionByName(factionName), boost);
        switch (result) {
            case SUCCESS:
                msg(player, "power.boost-set", "faction", factionName, "boost", boost);
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
