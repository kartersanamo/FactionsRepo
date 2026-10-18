package commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PowerboostSubCommand extends FactionSubCommand {
    public PowerboostSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("powerboost")
    @CommandPermission("factions.admin")
    public void onPowerboost(Player player, String factionName, int boost) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        factionSystem.setPowerBoost(faction, boost);
        msg(player, "power.boost-set", "faction", factionName, "boost", boost);
    }
}
