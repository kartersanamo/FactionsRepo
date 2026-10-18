package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PermanentSubCommand extends FactionSubCommand {
    public PermanentSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("permanent")
    @CommandPermission("factions.admin")
    public void onPermanent(Player player, String factionName) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { player.sendMessage("Faction not found."); return; }
        boolean next = !faction.isPermanent();
        factionSystem.setPermanent(faction, next);
        player.sendMessage("Permanent for " + factionName + ": " + next);
    }
}
