package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class AnnounceSubCommand extends FactionSubCommand {
    public AnnounceSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("announce|ann")
    public void onAnnounce(Player player, String message) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        factionSystem.broadcastToFaction(faction, "announce.format", "player", player.getName(), "message", message);
    }
}
