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
        catch (IllegalArgumentException ex) { player.sendMessage("Invalid rank. Use MEMBER, MOD, or COLEADER."); return; }
        if (rank == FactionRole.LEADER) { player.sendMessage("Default rank cannot be LEADER."); return; }
        if (!factionSystem.setDefaultRank(player, rank)) { player.sendMessage("Only the leader can set default rank."); return; }
        player.sendMessage("Default rank set to " + rank + ".");
    }
}
