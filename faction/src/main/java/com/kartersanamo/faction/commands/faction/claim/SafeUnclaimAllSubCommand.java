package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class SafeUnclaimAllSubCommand extends FactionSubCommand {
    public SafeUnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("safeunclaimall")
    @CommandPermission("factions.admin")
    public void onSafeUnclaimAll(Player player) {
        Faction faction = factionSystem.getFactionByName("Safezone");
        FactionResult result = factionSystem.unclaimAll(faction);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.safe-unclaim");
                break;
            case FACTION_NOT_FOUND:
                msg(player, "claim.safe-missing");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
