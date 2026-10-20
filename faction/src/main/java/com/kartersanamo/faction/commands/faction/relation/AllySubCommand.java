package com.kartersanamo.faction.commands.faction.relation;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class AllySubCommand extends FactionSubCommand {
    public AllySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ally")
    public void onCommand(Player player, String factionName) {
        FactionResult result = factionSystem.setRelation(player, factionName, "ally");
        switch (result) {
            case SUCCESS:
                msg(player, "relation.set", "relation", "ally", "faction", factionName);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            default:
                msg(player, "relation.failed");
                break;
        }
    }
}
