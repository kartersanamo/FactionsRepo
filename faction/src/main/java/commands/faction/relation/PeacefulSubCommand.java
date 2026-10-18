package commands.faction.relation;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PeacefulSubCommand extends FactionSubCommand {
    public PeacefulSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("peaceful")
    public void onPeaceful(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        boolean next = !faction.isPeaceful();
        if (!factionSystem.setPeaceful(player, next)) { player.sendMessage("Only the leader can toggle peaceful."); return; }
        player.sendMessage("Faction peaceful mode: " + next);
    }
}
