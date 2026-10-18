package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class UnclaimSubCommand extends FactionSubCommand {

    public UnclaimSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("unclaim")
    public void onUnclaimCommand(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction owner = factionSystem.getFactionAt(claim);
        if (owner == null) {
            msg(player, "claim.unclaim-none");
            return;
        }
        if (!owner.getId().equals(faction.getId())) {
            msg(player, "claim.unclaim-other", "faction", owner.getName());
            return;
        }

        if (!factionSystem.unclaimChunk(player, claim)) {
            msg(player, "claim.unclaim-failed");
            return;
        }
        msg(player, "claim.unclaim-success", "chunk", claim.getX(), "chunkz", claim.getZ());
    }
}
