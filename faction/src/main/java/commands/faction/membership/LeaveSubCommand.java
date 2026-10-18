package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
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

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member != null && member.isLeader()) {
            msg(player, "leave.leader");
            return;
        }

        if (!factionSystem.leaveFaction(player)) {
            msg(player, "leave.failed");
            return;
        }
        msg(player, "leave.success", "faction", faction.getName());
    }
}
