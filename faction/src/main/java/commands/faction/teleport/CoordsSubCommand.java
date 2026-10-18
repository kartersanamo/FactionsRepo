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
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        Location loc = player.getLocation();
        factionSystem.broadcastToFaction(faction, "teleport.coords-broadcast",
                "player", player.getName(), "x", loc.getBlockX(), "y", loc.getBlockY(), "z", loc.getBlockZ(),
                "world", loc.getWorld().getName());
        msg(player, "teleport.coords-shared");
    }
}
