package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class CornerSubCommand extends FactionSubCommand {
    public CornerSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("corner")
    public void onCorner(Player player) {
        int index = factionSystem.getFlags().nextCornerIndex(player.getUniqueId());
        Location location = factionSystem.getCornerLocation(player, index);
        if (location == null) { msg(player, "claim.corner-failed"); return; }
        player.teleport(location);
        msg(player, "claim.corner", "index", index + 1);
    }
}
