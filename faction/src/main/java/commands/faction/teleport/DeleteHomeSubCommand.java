package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DeleteHomeSubCommand extends FactionSubCommand {
    public DeleteHomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("delhome|deletehome")
    public void onDeleteHome(Player player) {
        if (!factionSystem.deleteHome(player)) {
            msg(player, "home.delete-failed");
            return;
        }
        msg(player, "home.deleted");
    }
}
