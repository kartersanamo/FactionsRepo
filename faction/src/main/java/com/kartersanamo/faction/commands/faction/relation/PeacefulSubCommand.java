package com.kartersanamo.faction.commands.faction.relation;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class PeacefulSubCommand extends FactionSubCommand {
    public PeacefulSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("peaceful")
    public void onPeaceful(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        boolean next = !faction.isPeaceful();
        FactionResult result = factionSystem.setPeaceful(player, next);
        switch (result) {
            case SUCCESS:
                msg(player, next ? "relation.peaceful-on" : "relation.peaceful-off");
                break;
            case NOT_LEADER:
                msg(player, "relation.peaceful-denied");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
