package com.kartersanamo.faction.commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class DisbandSubCommand extends FactionSubCommand {

    public DisbandSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("disband")
    public void onDisbandCommand(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        FactionResult result = factionSystem.disbandFaction(player);
        switch (result) {
            case SUCCESS:
                msg(player, "disband.success", "faction", faction.getName());
                break;
            case NOT_LEADER:
                msg(player, "disband.not-leader");
                break;
            case PERMANENT:
                msg(player, "disband.permanent");
                break;
            default:
                msg(player, "disband.failed");
                break;
        }
    }
}
