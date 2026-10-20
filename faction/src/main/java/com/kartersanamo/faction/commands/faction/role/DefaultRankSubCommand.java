package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import com.kartersanamo.faction.model.FactionRole;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class DefaultRankSubCommand extends FactionSubCommand {
    public DefaultRankSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("defaultrank")
    public void onDefaultRank(Player player, String rankName) {
        FactionRole rank;
        try { rank = FactionRole.valueOf(rankName.toUpperCase()); }
        catch (IllegalArgumentException ex) { msg(player, "defaultrank.invalid"); return; }
        if (rank == FactionRole.LEADER) { msg(player, "defaultrank.cannot-leader"); return; }
        if (rank == FactionRole.ALT) { msg(player, "defaultrank.invalid"); return; }
        FactionResult result = factionSystem.setDefaultRank(player, rank);
        switch (result) {
            case SUCCESS:
                msg(player, "defaultrank.success", "rank", rank);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "defaultrank.not-leader");
                break;
            case INVALID_RANK:
                msg(player, "defaultrank.invalid");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
