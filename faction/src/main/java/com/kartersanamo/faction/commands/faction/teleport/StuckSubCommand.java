package com.kartersanamo.faction.commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class StuckSubCommand extends FactionSubCommand {
    public StuckSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("stuck")
    public void onStuck(Player player) {
        Location location = factionSystem.findStuckTeleport(player);
        if (location == null) { msg(player, "teleport.stuck-failed"); return; }
        player.teleport(location);
        msg(player, "teleport.stuck");
    }
}
