package systems;

import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionSystem {

    private Map<UUID, Faction> factions = new HashMap<>();

    public FactionPlayer getFactionPlayer(Player player) {
        return new FactionPlayer(player);
    }

    public Faction getFaction(FactionPlayer fplayer) {
        for (Faction faction : factions.values()) {
            if (faction.getMembers().contains(fplayer)) {
                return faction;
            }
        }
        return null;
    }

    public Faction createFaction(FactionPlayer owner, String factionName) {
        Faction faction = new Faction(owner, factionName);
        factions.put(UUID.randomUUID(), faction);
        return faction;
    }

    public boolean doesFactionExist(String factionName) {
        for (Faction faction : factions.values()) {
            if (faction.getName().equalsIgnoreCase(factionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidFactionName(String factionName) {
        if (factionName == null || factionName.isEmpty()) {
            return  false;
        }

        if (factionName.length() < 3 || factionName.length() > 16) {
            return false;
        }

        if (factionName.contains(" ")) {
            return false;
        }

        for (char c : factionName.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
