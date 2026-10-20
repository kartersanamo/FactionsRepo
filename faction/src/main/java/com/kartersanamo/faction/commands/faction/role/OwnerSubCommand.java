package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class OwnerSubCommand extends FactionSubCommand {
    public OwnerSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("owner|leader")
    @CommandCompletion("@players")
    public void onOwner(Player player, String target) {
        FactionResult result = factionSystem.transferLeadership(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "owner.success", "player", target);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            case NOT_MEMBER:
                msg(player, "kick.not-member");
                break;
            default:
                msg(player, "owner.failed");
                break;
        }
    }
}
