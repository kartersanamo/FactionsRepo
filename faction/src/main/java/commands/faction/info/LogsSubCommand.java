package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class LogsSubCommand extends FactionSubCommand {
    public LogsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("logs")
    public void onLogs(Player player) {
        msg(player, "logs.none");
    }
}
