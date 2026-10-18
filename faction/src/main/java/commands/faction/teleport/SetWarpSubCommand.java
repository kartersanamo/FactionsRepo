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
            msg(player, "warp.set-failed");
            return;
        }
        msg(player, "warp.set", "warp", name);
    }
}
