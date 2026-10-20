package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ListSubCommand extends FactionSubCommand {
    public ListSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("list")
    public void onList(Player player) {
        msg(player, "list.header");
        int i = 0;
        for (Faction faction : factionSystem.listFactionsSortedByLand()) {
            msg(player, "list.line", "index", ++i, "faction", faction.getName(), "land", faction.getLand(),
                    "members", faction.getMainMemberCount(),
                    "open", faction.isOpen() ? messages.raw("list.open-tag") : "");
            if (i >= 20) break;
        }
        if (i == 0) msg(player, "list.none");
    }
}
