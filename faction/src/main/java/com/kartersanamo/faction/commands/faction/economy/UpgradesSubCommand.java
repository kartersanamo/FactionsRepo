package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class UpgradesSubCommand extends FactionSubCommand {
    public UpgradesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("upgrades")
    public void onUpgrades(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        msg(player, "upgrades.header", "faction", faction.getName());
        msg(player, "upgrades.vaults", "max", faction.getMaxVaults());
        msg(player, "upgrades.powerboost", "boost", faction.getPowerBoost());
    }
}
