package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ListSubCommand extends FactionSubCommand {
    public ListSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("list")
    public void onList(Player player) {
        player.sendMessage("Factions:");
        int i = 0;
        for (Faction faction : factionSystem.listFactionsSortedByLand()) {
            player.sendMessage(++i + ". " + faction.getName() + " - land " + faction.getLand()
                    + ", members " + faction.getMembers().size()
                    + (faction.isOpen() ? " [open]" : ""));
            if (i >= 20) break;
        }
        if (i == 0) player.sendMessage("No factions.");
    }
}
