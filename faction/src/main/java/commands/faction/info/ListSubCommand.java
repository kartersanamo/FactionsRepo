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
        msg(player, "list.header");
        int i = 0;
        for (Faction faction : factionSystem.listFactionsSortedByLand()) {
            msg(player, "list.line", "index", ++i, "faction", faction.getName(), "land", faction.getLand(),
                    "members", faction.getMembers().size(),
                    "open", faction.isOpen() ? messages.raw("list.open-tag") : "");
            if (i >= 20) break;
        }
        if (i == 0) msg(player, "list.none");
    }
}
