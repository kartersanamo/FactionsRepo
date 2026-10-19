package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetWarpSubCommand extends FactionSubCommand {
    public SetWarpSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setwarp")
    public void onSetWarp(Player player, String name) {
        FactionResult result = factionSystem.setWarp(player, name);
        switch (result) {
            case SUCCESS:
                msg(player, "warp.set", "warp", name);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            default:
                msg(player, "warp.set-failed");
                break;
        }
    }
}
