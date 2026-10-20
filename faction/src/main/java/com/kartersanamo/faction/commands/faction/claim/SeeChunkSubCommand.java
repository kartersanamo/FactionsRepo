package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

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
