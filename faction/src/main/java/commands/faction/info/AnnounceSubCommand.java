package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class AnnounceSubCommand extends FactionSubCommand {
    public AnnounceSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("announce|ann")
    public void onAnnounce(Player player, String message) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        factionSystem.broadcastToFaction(faction, "[Announcement] " + player.getName() + ": " + message);
    }
}
