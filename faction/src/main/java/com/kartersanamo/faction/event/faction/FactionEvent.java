package com.kartersanamo.faction.event.faction;

import com.kartersanamo.faction.model.Faction;
import org.bukkit.event.Event;

public abstract class FactionEvent extends Event {
    private final Faction faction;

    protected FactionEvent(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }
}
