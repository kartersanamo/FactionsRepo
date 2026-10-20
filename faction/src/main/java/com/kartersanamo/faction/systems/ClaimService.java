package com.kartersanamo.faction.systems;

import com.kartersanamo.faction.event.claim.LandClaimEvent;
import com.kartersanamo.faction.event.claim.LandUnclaimEvent;
import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

/**
 * Chunk claiming, unclaiming, base claim and build permission checks.
 */
public class ClaimService {
    private final FactionRegistry registry;

    public ClaimService(FactionRegistry registry) {
        this.registry = registry;
    }

    public FactionResult claimChunk(Player player, ChunkClaim claim) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        if (registry.claims().containsKey(claim.toKey())) {
            return FactionResult.ALREADY_CLAIMED;
        }
        if (faction.getLand() >= faction.getPower()) {
            return FactionResult.NOT_ENOUGH_POWER;
        }

        registry.claims().put(claim.toKey(), faction.getId());
        faction.getClaims().add(claim.toKey());
        Bukkit.getPluginManager().callEvent(new LandClaimEvent(faction, claim));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unclaimChunk(Player player, ChunkClaim claim) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionResult ownership = checkClaimOwnedBy(faction, claim);
        if (ownership != FactionResult.SUCCESS) {
            return ownership;
        }

        registry.claims().remove(claim.toKey());
        faction.getClaims().remove(claim.toKey());
        if (claim.toKey().equals(faction.getBaseClaimKey())) {
            faction.setBaseClaimKey(null);
        }
        Bukkit.getPluginManager().callEvent(new LandUnclaimEvent(faction, claim));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unclaimAll(Player player) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        return unclaimAll(faction);
    }

    public FactionResult unclaimAll(Faction faction) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        for (String claimKey : new HashSet<>(faction.getClaims())) {
            registry.claims().remove(claimKey);
            faction.getClaims().remove(claimKey);
        }
        if (faction.getBaseClaimKey() != null) {
            faction.setBaseClaimKey(null);
        }
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setBaseClaim(Player player, ChunkClaim claim) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionResult ownership = checkClaimOwnedBy(faction, claim);
        if (ownership != FactionResult.SUCCESS) {
            return ownership;
        }
        faction.setBaseClaimKey(claim.toKey());
        registry.save();
        return FactionResult.SUCCESS;
    }

    public boolean canBuild(Player player, ChunkClaim claim) {
        UUID ownerId = registry.claims().get(claim.toKey());
        if (ownerId == null) {
            return true;
        }
        UUID playerFactionId = registry.playerToFaction().get(player.getUniqueId());
        return ownerId.equals(playerFactionId);
    }

    private FactionResult checkClaimOwnedBy(Faction faction, ChunkClaim claim) {
        UUID ownerId = registry.claims().get(claim.toKey());
        if (ownerId == null) {
            return FactionResult.NOT_CLAIMED;
        }
        if (!ownerId.equals(faction.getId())) {
            return FactionResult.OTHER_CLAIM;
        }
        return FactionResult.SUCCESS;
    }
}
