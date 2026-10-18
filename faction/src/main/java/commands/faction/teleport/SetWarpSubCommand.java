package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetWarpSubCommand extends FactionSubCommand {
    public SetWarpSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setwarp")
    public void onSetWarp(Player player, String name) {
        if (!factionSystem.setWarp(player, name)) {
            player.sendMessage("Could not set warp.");
            return;
        }
        player.sendMessage("Warp " + name + " set.");
    }
}
