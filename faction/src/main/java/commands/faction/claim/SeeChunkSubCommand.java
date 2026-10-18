package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SeeChunkSubCommand extends FactionSubCommand {
    public SeeChunkSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("seechunk")
    public void onSeeChunk(Player player) {
        boolean enabled = factionSystem.getFlags().toggleSeeChunk(player.getUniqueId());
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction owner = factionSystem.getFactionAt(claim);
        msg(player, "claim.seechunk", "state", enabled ? "on" : "off",
                "chunk", claim.getX(), "chunkz", claim.getZ(),
                "owner", owner == null ? messages.raw("general.wilderness") : owner.getName());
    }
}
