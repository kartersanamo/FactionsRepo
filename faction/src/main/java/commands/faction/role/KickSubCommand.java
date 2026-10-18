package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class KickSubCommand extends FactionSubCommand {

    public KickSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("kick")
    @CommandCompletion("@players")
    public void onKickCommand(Player player, String targetName) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            player.sendMessage("Only the faction leader can kick.");
            return;
        }

        if (player.getName().equalsIgnoreCase(targetName)) {
            player.sendMessage("You cannot kick yourself.");
            return;
        }

        FactionPlayer target = null;
        for (FactionPlayer factionMember : faction.getMembers()) {
            if (factionMember.getName().equalsIgnoreCase(targetName)) {
                target = factionMember;
                break;
            }
        }
        if (target == null) {
            player.sendMessage("That player is not in your faction.");
            return;
        }
        if (target.isLeader()) {
            player.sendMessage("You cannot kick the faction leader.");
            return;
        }

        if (!factionSystem.kickPlayer(player, targetName)) {
            player.sendMessage("Could not kick that player.");
            return;
        }

        player.sendMessage("Kicked " + target.getName() + " from the faction.");
        Player onlineTarget = target.getPlayer();
        if (onlineTarget != null) {
            onlineTarget.sendMessage("You were kicked from " + faction.getName() + ".");
        }
    }
}
