package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class CornerSubCommand extends FactionSubCommand {
    public CornerSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("corner")
    public void onCorner(Player player) {
        int index = factionSystem.getFlags().nextCornerIndex(player.getUniqueId());
        Location location = factionSystem.getCornerLocation(player, index);
        if (location == null) { player.sendMessage("Could not find corner."); return; }
        player.teleport(location);
        player.sendMessage("Teleported to corner " + (index + 1) + "/4.");
    }
}
