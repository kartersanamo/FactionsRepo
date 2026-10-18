package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionRole;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ModSubCommand extends FactionSubCommand {
    public ModSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("mod|officer")
    @CommandCompletion("@players")
    public void onMod(Player player, String target) {
        if (!factionSystem.setRole(player, target, FactionRole.MOD)) {
            player.sendMessage("Could not set mod.");
            return;
        }
        player.sendMessage(target + " is now a mod.");
    }
}
