package com.kartersanamo.faction.event.claim;

import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Faction faction;
    private final ChunkClaim claim;

    public LandUnclaimEvent(Faction faction, ChunkClaim claim) {
        this.faction = faction;
        this.claim = claim;
    }

    public Faction getFaction() {
        return faction;
    }

    public ChunkClaim getClaim() {
        return claim;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
