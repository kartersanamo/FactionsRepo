package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetBaseClaimSubCommand extends FactionSubCommand {
    public SetBaseClaimSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setbase|baseclaim")
    public void onSetBase(Player player) {
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        if (!factionSystem.setBaseClaim(player, claim)) {
            msg(player, "claim.base-failed");
            return;
        }
        msg(player, "claim.base-set", "chunk", claim.getX(), "chunkz", claim.getZ());
    }
}
