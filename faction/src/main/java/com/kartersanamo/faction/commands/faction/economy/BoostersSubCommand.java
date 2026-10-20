package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class BoostersSubCommand extends FactionSubCommand {
    public BoostersSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("boosters")
    public void onBoosters(Player player) {
        msg(player, "boosters.none");
    }
}
