package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class PromoteSubCommand extends FactionSubCommand {
    public PromoteSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("promote")
    @CommandCompletion("@players")
    public void onPromote(Player player, String target) {
        FactionResult result = factionSystem.promoteMember(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "promote.success", "player", target);
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
                msg(player, "promote.failed");
                break;
        }
    }
}
