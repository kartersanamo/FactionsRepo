package commands.faction.membership;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class InviteSubCommand extends FactionSubCommand {

    public InviteSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void onInviteCommand(Player player, String targetName) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            player.sendMessage("Only the faction leader can invite.");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage("Player not found.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot invite yourself.");
            return;
        }
        if (factionSystem.getFaction(target.getUniqueId()) != null) {
            player.sendMessage("That player is already in a faction.");
            return;
        }

        if (!factionSystem.invitePlayer(player, target)) {
            player.sendMessage("Could not invite that player.");
            return;
        }

        player.sendMessage("Invited " + target.getName() + " to " + faction.getName() + ".");
        target.sendMessage("You were invited to " + faction.getName() + ". Use /f join " + faction.getName());
    }
}
