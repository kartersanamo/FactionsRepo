package com.kartersanamo.faction.event.player;

import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import org.bukkit.event.Event;

public abstract class FactionPlayerEvent extends Event {
    private final Faction faction;
    private final FactionPlayer factionPlayer;

    protected FactionPlayerEvent(Faction faction, FactionPlayer factionPlayer) {
        this.faction = faction;
        this.factionPlayer = factionPlayer;
    }

    public Faction getFaction() {
        return faction;
    }

    public FactionPlayer getFactionPlayer() {
        return factionPlayer;
    }
}
