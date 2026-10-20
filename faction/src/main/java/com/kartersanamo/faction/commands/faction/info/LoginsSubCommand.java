package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class LoginsSubCommand extends FactionSubCommand {
    public LoginsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("logins")
    public void onLogins(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        msg(player, "logins.header");
        for (FactionPlayer member : faction.getOnlineMembers()) {
            msg(player, "logins.line", "player", member.getName());
        }
    }
}
