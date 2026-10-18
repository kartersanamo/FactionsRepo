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
            msg(player, "general.not-in-faction");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            msg(player, "kick.not-leader");
            return;
        }

        if (player.getName().equalsIgnoreCase(targetName)) {
            msg(player, "kick.self");
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
            msg(player, "kick.not-member");
            return;
        }
        if (target.isLeader()) {
            msg(player, "kick.is-leader");
            return;
        }

        if (!factionSystem.kickPlayer(player, targetName)) {
            msg(player, "kick.failed");
            return;
        }

        msg(player, "kick.success", "player", target.getName());
        Player onlineTarget = target.getPlayer();
        if (onlineTarget != null) {
            msg(onlineTarget, "kick.notified", "faction", faction.getName());
        }
    }
}
