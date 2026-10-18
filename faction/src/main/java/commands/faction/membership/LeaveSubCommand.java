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
            player.sendMessage("You are not in a faction.");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member != null && member.isLeader()) {
            player.sendMessage("Leaders must use /f disband.");
            return;
        }

        if (!factionSystem.leaveFaction(player)) {
            player.sendMessage("Could not leave the faction.");
            return;
        }
        player.sendMessage("You left the faction.");
    }
}
