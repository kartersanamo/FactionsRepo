package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class LogsSubCommand extends FactionSubCommand {
    public LogsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("logs")
    public void onLogs(Player player) {
        msg(player, "logs.none");
    }
}
