package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DisbandSubCommand extends FactionSubCommand {

    public DisbandSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("disband")
    public void onDisbandCommand(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            msg(player, "disband.not-leader");
            return;
        }

        String name = faction.getName();
        if (!factionSystem.disbandFaction(player)) {
            msg(player, "disband.failed");
            return;
        }
        msg(player, "disband.success", "faction", name);
    }
}
