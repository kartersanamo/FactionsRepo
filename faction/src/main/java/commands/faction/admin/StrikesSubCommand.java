package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class StrikesSubCommand extends FactionSubCommand {
    public StrikesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("strikes")
    @CommandPermission("factions.admin")
    public void onStrikes(Player player, String factionName, @Optional Integer value) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        if (value == null) {
            msg(player, "admin.strikes-view", "faction", factionName, "strikes", faction.getStrikes());
            return;
        }
        factionSystem.adminSetStrikes(faction, value);
        msg(player, "admin.strikes-set", "faction", factionName, "strikes", value);
    }
}
