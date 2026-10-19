package commands.faction.membership;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class AltSubCommand extends FactionSubCommand {

    public AltSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("alt")
    @CommandCompletion("@players")
    public void onAltCommand(Player player, String targetName) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canInvite()) {
            msg(player, "alt.not-leader");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            msg(player, "general.player-not-found");
            return;
        }
        if (target.equals(player)) {
            msg(player, "alt.self");
            return;
        }
        if (factionSystem.getFaction(target.getUniqueId()) != null) {
            msg(player, "alt.already-member");
            return;
        }

        if (!factionSystem.inviteAlt(player, target)) {
            msg(player, "alt.failed");
            return;
        }

        msg(player, "alt.success", "player", target.getName(), "faction", faction.getName());
        msg(target, "alt.notified", "faction", faction.getName());
    }
}
