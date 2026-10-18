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
            player.sendMessage("You are not in a faction.");
            return;
        }

        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction owner = factionSystem.getFactionAt(claim);
        if (owner == null) {
            player.sendMessage("This chunk is not claimed.");
            return;
        }
        if (!owner.getId().equals(faction.getId())) {
            player.sendMessage("This chunk belongs to " + owner.getName() + ".");
            return;
        }

        if (!factionSystem.unclaimChunk(player, claim)) {
            player.sendMessage("Could not unclaim this chunk.");
            return;
        }
        player.sendMessage("Unclaimed chunk " + claim.getX() + ", " + claim.getZ() + ".");
    }
}
