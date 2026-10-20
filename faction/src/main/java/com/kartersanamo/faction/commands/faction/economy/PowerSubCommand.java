package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class PowerSubCommand extends FactionSubCommand {
    public PowerSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("power")
    public void onPower(Player player, @Optional String target) {
        Faction faction = target == null || target.isEmpty()
                ? factionSystem.getFaction(player.getUniqueId())
                : factionSystem.findFactionByNameOrPlayer(target);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        msg(player, "power.info", "faction", faction.getName(), "power", faction.getPower(),
                "max", faction.getMaxPower(), "boost", faction.getPowerBoost(), "land", faction.getLand());
    }
}
