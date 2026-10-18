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
        if (faction == null) { player.sendMessage("Faction not found."); return; }
        if (value == null) {
            player.sendMessage(factionName + " strikes: " + faction.getStrikes());
            return;
        }
        factionSystem.adminSetStrikes(faction, value);
        player.sendMessage("Set strikes for " + factionName + " to " + value + ".");
    }
}
