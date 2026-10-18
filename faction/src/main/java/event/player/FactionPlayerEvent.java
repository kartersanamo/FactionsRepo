package event.player;

import model.Faction;
import model.FactionPlayer;
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
