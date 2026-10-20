package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.FactionResult;
import com.kartersanamo.faction.model.FactionRole;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ColeaderSubCommand extends FactionSubCommand {
    public ColeaderSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("coleader|co-leader")
    @CommandCompletion("@players")
    public void onColeader(Player player, String target) {
        FactionResult result = factionSystem.setRole(player, target, FactionRole.COLEADER);
        switch (result) {
            case SUCCESS:
                msg(player, "coleader.success", "player", target);
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
                msg(player, "coleader.failed");
                break;
        }
    }
}
