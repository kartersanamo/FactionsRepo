package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class WildSubCommand extends FactionSubCommand {
    public WildSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("wild")
    public void onWild(Player player) {
        Location location = factionSystem.findWildernessTeleport(player);
        if (location == null) { player.sendMessage("Could not find wilderness."); return; }
        player.teleport(location);
        player.sendMessage("Teleported to wilderness.");
    }
}
