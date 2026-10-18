package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class StuckSubCommand extends FactionSubCommand {
    public StuckSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("stuck")
    public void onStuck(Player player) {
        Location location = factionSystem.findStuckTeleport(player);
        if (location == null) { player.sendMessage("Could not find a safe spot."); return; }
        player.teleport(location);
        player.sendMessage("Teleported out of claimed land.");
    }
}
