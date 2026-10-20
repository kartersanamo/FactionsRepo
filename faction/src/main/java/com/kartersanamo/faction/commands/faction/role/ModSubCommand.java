package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import com.kartersanamo.faction.model.FactionRole;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ModSubCommand extends FactionSubCommand {
    public ModSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("mod|officer")
    @CommandCompletion("@players")
    public void onMod(Player player, String target) {
        FactionResult result = factionSystem.setRole(player, target, FactionRole.MOD);
        switch (result) {
            case SUCCESS:
                msg(player, "mod.success", "player", target);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case NOT_MEMBER:
                msg(player, "kick.not-member");
                break;
            default:
                msg(player, "mod.failed");
                break;
        }
    }
}
