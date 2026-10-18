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
            if (warps.isEmpty()) { player.sendMessage("No warps. Usage: /f warp <name>"); return; }
            player.sendMessage("Warps: " + String.join(", ", warps));
            return;
        }
        if (!factionSystem.teleportWarp(player, name)) {
            player.sendMessage("Warp not found.");
            return;
        }
        player.sendMessage("Warped to " + name + ".");
    }
}
