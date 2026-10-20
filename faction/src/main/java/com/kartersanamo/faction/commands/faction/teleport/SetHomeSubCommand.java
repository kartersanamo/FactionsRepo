package com.kartersanamo.faction.commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class SetHomeSubCommand extends FactionSubCommand {
    public SetHomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("sethome")
    public void onSetHome(Player player) {
        FactionResult result = factionSystem.setHome(player);
        switch (result) {
            case SUCCESS:
                msg(player, "home.set");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case NOT_YOUR_CLAIM:
                msg(player, "home.not-your-claim");
                break;
            default:
                msg(player, "home.set-failed");
                break;
        }
    }
}
