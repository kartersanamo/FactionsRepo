package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

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
