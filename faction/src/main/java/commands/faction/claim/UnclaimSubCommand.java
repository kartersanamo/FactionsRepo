package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class UnclaimSubCommand extends FactionSubCommand {

    public UnclaimSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("unclaim")
    public void onUnclaimCommand(Player player) {
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        FactionResult result = factionSystem.unclaimChunk(player, claim);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.unclaim-success", "chunk", claim.getX(), "chunkz", claim.getZ());
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case NOT_CLAIMED:
                msg(player, "claim.unclaim-none");
                break;
            case OTHER_CLAIM:
                Faction owner = factionSystem.getFactionAt(claim);
                msg(player, "claim.unclaim-other", "faction", owner != null ? owner.getName() : "?");
                break;
            default:
                msg(player, "claim.unclaim-failed");
                break;
        }
    }
}
