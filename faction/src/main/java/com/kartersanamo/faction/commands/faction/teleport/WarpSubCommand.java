package com.kartersanamo.faction.commands.faction.teleport;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

import java.util.List;

public class WarpSubCommand extends FactionSubCommand {
    public WarpSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("warp")
    public void onWarp(Player player, @Optional String name) {
        if (name == null || name.isEmpty()) {
            List<String> warps = factionSystem.listWarps(player);
            if (warps.isEmpty()) { msg(player, "warp.none"); return; }
            msg(player, "warp.list", "list", String.join(", ", warps));
            return;
        }
        FactionResult result = factionSystem.teleportWarp(player, name);
        switch (result) {
            case SUCCESS:
                msg(player, "warp.teleport", "warp", name);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case WARP_MISSING:
                msg(player, "warp.missing");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
