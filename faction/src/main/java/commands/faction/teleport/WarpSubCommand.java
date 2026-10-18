package commands.faction.teleport;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

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
        if (!factionSystem.teleportWarp(player, name)) {
            msg(player, "warp.missing");
            return;
        }
        msg(player, "warp.teleport", "warp", name);
    }
}
