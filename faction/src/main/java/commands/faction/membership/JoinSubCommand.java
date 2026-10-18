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
            player.sendMessage("You are already in a faction.");
            return;
        }

        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) {
            player.sendMessage("Faction not found.");
            return;
        }

        if (!factionSystem.getInvites(player.getUniqueId()).contains(faction.getId())) {
            player.sendMessage("You do not have an invite to that faction.");
            return;
        }

        if (!factionSystem.joinFaction(player, factionName)) {
            player.sendMessage("Could not join the faction.");
            return;
        }
        player.sendMessage("You joined " + faction.getName() + ".");
    }
}
