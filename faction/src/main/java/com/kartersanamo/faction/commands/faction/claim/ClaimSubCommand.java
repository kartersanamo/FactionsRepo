package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ClaimSubCommand extends FactionSubCommand {

    public ClaimSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("claim")
    public void onClaimCommand(Player player, @Optional Integer radius) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        if (radius == null || radius == 1) {
            performClaim(ChunkClaim.from(player.getLocation()), player);
            return;
        }

        if (radius == 0) {
            msg(player, "claim.radius-zero");
            return;
        }

        radius = radius - 1; // center chunk is included in the radius loop
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkClaim claim = ChunkClaim.from(player.getLocation().add(x * 16, 0, z * 16));
                performClaim(claim, player);
            }
        }
    }

    private void performClaim(ChunkClaim claim, Player player) {
        FactionResult result = factionSystem.claimChunk(player, claim);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.success", "chunk", claim.getX(), "chunkz", claim.getZ());
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case ALREADY_CLAIMED:
                Faction owner = factionSystem.getFactionAt(claim);
                msg(player, "claim.already", "faction", owner != null ? owner.getName() : "?");
                break;
            case NOT_ENOUGH_POWER:
                msg(player, "claim.failed");
                break;
            default:
                msg(player, "claim.failed");
                break;
        }
    }
}
