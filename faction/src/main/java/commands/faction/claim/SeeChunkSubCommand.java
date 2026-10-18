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
        player.sendMessage("SeeChunk " + (enabled ? "on" : "off") + ". Current chunk: "
                + claim.getX() + ", " + claim.getZ()
                + " (" + (owner == null ? "Wilderness" : owner.getName()) + ")");
    }
}
