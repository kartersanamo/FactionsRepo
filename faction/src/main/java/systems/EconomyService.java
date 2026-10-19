package systems;

import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import org.bukkit.entity.Player;

/**
 * Faction money, TNT, points, power boost, strikes and vault limits.
 */
public class EconomyService {
    private final FactionRegistry registry;

    public EconomyService(FactionRegistry registry) {
        this.registry = registry;
    }

    public FactionResult depositMoney(Player player, double amount) {
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        faction.setMoney(faction.getMoney() + amount);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult withdrawMoney(Player player, double amount) {
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        if (faction.getMoney() < amount) {
            return FactionResult.NOT_ENOUGH;
        }
        faction.setMoney(faction.getMoney() - amount);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult addTnt(Faction faction, int amount) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setTnt(faction.getTnt() + amount);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult removeTnt(Faction faction, int amount) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        if (faction.getTnt() < amount) {
            return FactionResult.NOT_ENOUGH;
        }
        faction.setTnt(faction.getTnt() - amount);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPoints(Faction faction, int points) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPoints(points);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPowerBoost(Faction faction, int boost) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPowerBoost(boost);
        faction.recalculatePower();
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult addStrikes(Faction faction, int strikes) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (strikes <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setStrikes(faction.getStrikes() + strikes);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult adminSetStrikes(Faction faction, int strikes) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (strikes < 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setStrikes(strikes);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setMaxVaults(Faction faction, int maxVaults) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setMaxVaults(maxVaults);
        registry.save();
        return FactionResult.SUCCESS;
    }
}
