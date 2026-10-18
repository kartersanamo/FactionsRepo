package event.faction;

import model.Faction;
import org.bukkit.event.HandlerList;

public class FactionDisbandEvent extends FactionEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public FactionDisbandEvent(Faction faction) {
        super(faction);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
