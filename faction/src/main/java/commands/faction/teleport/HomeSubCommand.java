package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class HomeSubCommand extends FactionSubCommand {
    public HomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("home")
    public void onHome(Player player) {
        if (!factionSystem.teleportHome(player)) {
            player.sendMessage("No faction home set or you are not in a faction.");
            return;
        }
        player.sendMessage("Teleported home.");
    }
}
