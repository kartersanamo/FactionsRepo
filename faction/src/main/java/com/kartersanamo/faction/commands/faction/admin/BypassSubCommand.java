package com.kartersanamo.faction.commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class BypassSubCommand extends FactionSubCommand {
    public BypassSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("bypass")
    @CommandPermission("factions.admin")
    public void onBypass(Player player) {
        boolean enabled = factionSystem.getFlags().toggleBypass(player.getUniqueId());
        msg(player, enabled ? "admin.bypass-on" : "admin.bypass-off");
    }
}
