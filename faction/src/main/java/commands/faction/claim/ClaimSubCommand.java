package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ClaimSubCommand extends FactionSubCommand {

    public ClaimSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("claim")
    public void onClaimCommand(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction owner = factionSystem.getFactionAt(claim);
        if (owner != null) {
            msg(player, "claim.already", "faction", owner.getName());
            return;
        }

        if (!factionSystem.claimChunk(player, claim)) {
            msg(player, "claim.failed");
            return;
        }
        msg(player, "claim.success", "chunk", claim.getX(), "chunkz", claim.getZ());
    }
}
