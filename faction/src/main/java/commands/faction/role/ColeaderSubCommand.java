package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionRole;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ColeaderSubCommand extends FactionSubCommand {
    public ColeaderSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("coleader|co-leader")
    @CommandCompletion("@players")
    public void onColeader(Player player, String target) {
        if (!factionSystem.setRole(player, target, FactionRole.COLEADER)) {
            player.sendMessage("Could not set coleader.");
            return;
        }
        player.sendMessage(target + " is now a coleader.");
    }
}
