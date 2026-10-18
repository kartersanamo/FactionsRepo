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
            player.sendMessage("You are not in a faction.");
            return;
        }

        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction owner = factionSystem.getFactionAt(claim);
        if (owner != null) {
            player.sendMessage("This chunk is already claimed by " + owner.getName() + ".");
            return;
        }

        if (!factionSystem.claimChunk(player, claim)) {
            player.sendMessage("Could not claim this chunk.");
            return;
        }
        player.sendMessage("Claimed chunk " + claim.getX() + ", " + claim.getZ() + ".");
    }
}
