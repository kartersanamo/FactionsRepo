package commands.faction.info;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

import java.util.List;

public class TopSubCommand extends FactionSubCommand {
    public TopSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("top")
    public void onTop(Player player, @Default("land") String type) {
        List<Faction> list;
        String label;
        if (type.equalsIgnoreCase("power")) {
            list = factionSystem.listFactionsSortedByPower();
            label = "power";
        } else if (type.equalsIgnoreCase("points")) {
            list = factionSystem.listFactionsSortedByPoints();
            label = "points";
        } else {
            list = factionSystem.listFactionsSortedByLand();
            label = "land";
        }
        player.sendMessage("Top factions by " + label + ":");
        int i = 0;
        for (Faction faction : list) {
            int value = label.equals("power") ? faction.getPower()
                    : label.equals("points") ? faction.getPoints() : faction.getLand();
            player.sendMessage(++i + ". " + faction.getName() + " - " + value);
            if (i >= 10) break;
        }
    }
}
