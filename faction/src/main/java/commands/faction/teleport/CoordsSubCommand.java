package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class CoordsSubCommand extends FactionSubCommand {
    public CoordsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("coords")
    public void onCoords(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        Location loc = player.getLocation();
        String message = player.getName() + " coords: "
                + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                + " (" + loc.getWorld().getName() + ")";
        factionSystem.broadcastToFaction(faction, message);
        player.sendMessage("Shared coords with your faction.");
    }
}
