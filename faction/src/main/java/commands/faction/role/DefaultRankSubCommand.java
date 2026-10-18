package commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionRole;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DefaultRankSubCommand extends FactionSubCommand {
    public DefaultRankSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("defaultrank")
    public void onDefaultRank(Player player, String rankName) {
        FactionRole rank;
        try { rank = FactionRole.valueOf(rankName.toUpperCase()); }
        catch (IllegalArgumentException ex) { msg(player, "defaultrank.invalid"); return; }
        if (rank == FactionRole.LEADER) { msg(player, "defaultrank.cannot-leader"); return; }
        if (!factionSystem.setDefaultRank(player, rank)) { msg(player, "defaultrank.not-leader"); return; }
        msg(player, "defaultrank.success", "rank", rank);
    }
}
