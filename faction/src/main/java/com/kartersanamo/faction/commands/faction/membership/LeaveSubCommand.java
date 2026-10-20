package com.kartersanamo.faction.commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class LeaveSubCommand extends FactionSubCommand {

    public LeaveSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("leave")
    public void onLeaveCommand(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        FactionResult result = factionSystem.leaveFaction(player);
        switch (result) {
            case SUCCESS:
                msg(player, "leave.success", "faction", faction.getName());
                break;
            case IS_LEADER:
                msg(player, "leave.leader");
                break;
            default:
                msg(player, "leave.failed");
                break;
        }
    }
}
