package com.kartersanamo.faction.systems;

import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionRelation;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Ally/enemy/truce relations and faction flags (peaceful, permanent, grace).
 */
public class RelationService {
    private final FactionRegistry registry;

    public RelationService(FactionRegistry registry) {
        this.registry = registry;
    }

    public FactionResult setRelation(Player actor, String otherFactionName, String relationType) {
        FactionResult leaderCheck = registry.checkLeader(actor);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (relationType == null) {
            return FactionResult.FAILED;
        }
        Faction faction = registry.getFaction(actor.getUniqueId());
        Faction other = registry.getFactionByName(otherFactionName);
        if (other == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (other.getId().equals(faction.getId())) {
            return FactionResult.SELF;
        }

        clearRelationsBetween(faction, other);
        String type = relationType.toLowerCase();
        if ("ally".equals(type)) {
            faction.getAllyIds().add(other.getId());
            other.getAllyIds().add(faction.getId());
        } else if ("enemy".equals(type)) {
            faction.getEnemyIds().add(other.getId());
            other.getEnemyIds().add(faction.getId());
        } else if ("truce".equals(type)) {
            faction.getTruceIds().add(other.getId());
            other.getTruceIds().add(faction.getId());
        } else if (!"neutral".equals(type)) {
            return FactionResult.FAILED;
        }
        registry.save();
        return FactionResult.SUCCESS;
    }

    /**
     * Relation of {@code other} as seen by {@code viewer}.
     * Null factions are treated as neutral (e.g. wilderness).
     */
    public FactionRelation getRelation(Faction viewer, Faction other) {
        if (viewer == null || other == null) {
            return FactionRelation.NEUTRAL;
        }
        if (viewer.getId().equals(other.getId())) {
            return FactionRelation.SELF;
        }
        if (viewer.getAllyIds().contains(other.getId())) {
            return FactionRelation.ALLY;
        }
        if (viewer.getTruceIds().contains(other.getId())) {
            return FactionRelation.TRUCE;
        }
        if (viewer.getEnemyIds().contains(other.getId())) {
            return FactionRelation.ENEMY;
        }
        return FactionRelation.NEUTRAL;
    }

    public ChatColor getRelationColor(Faction viewer, Faction other) {
        return getRelation(viewer, other).getColor();
    }

    public FactionResult setPeaceful(Player player, boolean peaceful) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        registry.getFaction(player.getUniqueId()).setPeaceful(peaceful);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPermanent(Faction faction, boolean permanent) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPermanent(permanent);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setGrace(Faction faction, boolean grace) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setGrace(grace);
        registry.save();
        return FactionResult.SUCCESS;
    }

    void clearRelationsBetween(Faction first, Faction second) {
        first.getAllyIds().remove(second.getId());
        second.getAllyIds().remove(first.getId());
        first.getEnemyIds().remove(second.getId());
        second.getEnemyIds().remove(first.getId());
        first.getTruceIds().remove(second.getId());
        second.getTruceIds().remove(first.getId());
    }

    void removeRelationsForFaction(UUID factionId) {
        for (Faction other : registry.factions().values()) {
            if (other.getId().equals(factionId)) {
                continue;
            }
            other.getAllyIds().remove(factionId);
            other.getEnemyIds().remove(factionId);
            other.getTruceIds().remove(factionId);
        }
    }
}
