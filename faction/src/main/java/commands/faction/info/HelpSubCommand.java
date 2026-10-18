package commands.faction.info;

import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class HelpSubCommand extends FactionSubCommand {
    public HelpSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @HelpCommand
    @Subcommand("help")
    public void onHelp(Player player) {
        msg(player, "help");
    }
}
