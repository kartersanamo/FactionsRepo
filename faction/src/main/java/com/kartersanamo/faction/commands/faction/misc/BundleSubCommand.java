package com.kartersanamo.faction.commands.faction.misc;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class BundleSubCommand extends FactionSubCommand {
    public BundleSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("bundle")
    public void onBundle(Player player) {
        msg(player, "misc.bundle");
    }
}
