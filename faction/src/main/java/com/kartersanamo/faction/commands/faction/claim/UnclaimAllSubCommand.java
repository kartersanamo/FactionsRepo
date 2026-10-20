package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class UnclaimAllSubCommand extends FactionSubCommand {
    public UnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("unclaimall")
    public void onUnclaimAll(Player player) {
        FactionResult result = factionSystem.unclaimAll(player);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.unclaim-all");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            default:
                msg(player, "claim.unclaim-all-failed");
                break;
        }
    }
}
