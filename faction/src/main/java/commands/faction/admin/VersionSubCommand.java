package commands.faction.admin;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class VersionSubCommand extends FactionSubCommand {
    public VersionSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("version")
    public void onVersion(Player player) {
        msg(player, "admin.version", "version", factionSystem.getPlugin().getDescription().getFullName());
    }
}
