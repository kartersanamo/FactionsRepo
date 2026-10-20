package com.kartersanamo.faction.commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ReloadSubCommand extends FactionSubCommand {
    public ReloadSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("reload")
    @CommandPermission("factions.admin")
    public void onReload(Player player) {
        factionSystem.save();
        factionSystem.load();
        factionSystem.getMessages().reload();
        msg(player, "admin.reloaded");
    }
}
