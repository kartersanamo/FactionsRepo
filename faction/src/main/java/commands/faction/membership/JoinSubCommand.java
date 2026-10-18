package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class JoinSubCommand extends FactionSubCommand {

    public JoinSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("join")
    public void onJoinCommand(Player player, String factionName) {
        if (factionSystem.getFaction(player.getUniqueId()) != null) {
            msg(player, "general.already-in-faction");
            return;
        }

        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) {
            msg(player, "general.faction-not-found");
            return;
        }
        if (faction.getBanned().contains(player.getUniqueId())) {
            msg(player, "join.banned");
            return;
        }

        boolean invited = factionSystem.getInvites(player.getUniqueId()).contains(faction.getId());
        if (!faction.isOpen() && !invited) {
            msg(player, "join.no-invite");
            return;
        }

        if (!factionSystem.joinFaction(player, factionName)) {
            msg(player, "join.failed");
            return;
        }
        msg(player, "join.success", "faction", faction.getName());
    }
}
