package com.kartersanamo.faction.commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class FlySubCommand extends FactionSubCommand {
    public FlySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("fly")
    public void onFly(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        Faction at = factionSystem.getFactionAt(ChunkClaim.from(player.getLocation()));
        if (at == null || !at.getId().equals(faction.getId())) {
            msg(player, "teleport.fly-denied");
            return;
        }
        boolean enabled = factionSystem.getFlags().toggleFly(player.getUniqueId());
        player.setAllowFlight(enabled);
        player.setFlying(enabled);
        msg(player, enabled ? "teleport.fly-on" : "teleport.fly-off");
    }
}
