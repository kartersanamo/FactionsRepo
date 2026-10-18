package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class CreateFactionSubCommand extends FactionSubCommand {

    public CreateFactionSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("create")
    public void onCreateCommand(Player player, String factionName) {
        FactionPlayer fplayer = factionSystem.getFactionPlayer(player);
        if (factionSystem.getFaction(fplayer) != null) {
            player.sendMessage("You are already in a faction.");
            return;
        }

        if (factionSystem.doesFactionExist(factionName)) {
            player.sendMessage("A faction with that name already exists.");
            return;
        }

        if (!factionSystem.isValidFactionName(factionName)) {
            player.sendMessage("Invalid faction name.");
            return;
        }

        Faction createdFaction = factionSystem.createFaction(fplayer, factionName);
        player.sendMessage("Faction " + createdFaction.getName() + " has been created!");
    }
}
