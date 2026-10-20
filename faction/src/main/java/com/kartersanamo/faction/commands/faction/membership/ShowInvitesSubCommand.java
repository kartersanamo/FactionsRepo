package com.kartersanamo.faction.commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

import java.util.List;

public class ShowInvitesSubCommand extends FactionSubCommand {
    public ShowInvitesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("invites|showinvites")
    public void onShowInvites(Player player) {
        List<String> invites = factionSystem.getInviteFactionNames(player);
        if (invites.isEmpty()) { msg(player, "invites.none"); return; }
        msg(player, "invites.list", "list", String.join(", ", invites));
    }
}
