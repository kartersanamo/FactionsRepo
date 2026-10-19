package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetBaseClaimSubCommand extends FactionSubCommand {
    public SetBaseClaimSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setbase|baseclaim")
    public void onSetBase(Player player) {
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        FactionResult result = factionSystem.setBaseClaim(player, claim);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.base-set", "chunk", claim.getX(), "chunkz", claim.getZ());
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            default:
                msg(player, "claim.base-failed");
                break;
        }
    }
}
