package com.kartersanamo.faction.commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ConfigSubCommand extends FactionSubCommand {
    public ConfigSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("config")
    @CommandPermission("factions.admin")
    public void onConfig(Player player) {
        msg(player, "admin.config");
    }
}
