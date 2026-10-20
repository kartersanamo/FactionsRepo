package com.kartersanamo.faction.event.player;

import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import org.bukkit.event.HandlerList;

public class FactionPlayerLeaveEvent extends FactionPlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public FactionPlayerLeaveEvent(Faction faction, FactionPlayer factionPlayer) {
        super(faction, factionPlayer);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
