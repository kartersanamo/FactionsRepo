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
            msg(player, "general.not-in-faction");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            msg(player, "invite.not-leader");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            msg(player, "general.player-not-found");
            return;
        }
        if (target.equals(player)) {
            msg(player, "invite.self");
            return;
        }
        if (factionSystem.getFaction(target.getUniqueId()) != null) {
            msg(player, "invite.already-member");
            return;
        }

        if (!factionSystem.invitePlayer(player, target)) {
            msg(player, "invite.failed");
            return;
        }

        msg(player, "invite.success", "player", target.getName(), "faction", faction.getName());
        msg(target, "invite.notified", "faction", faction.getName());
    }
}
