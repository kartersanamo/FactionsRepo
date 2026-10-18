package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import lombok.RequiredArgsConstructor;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CreateFactionSubCommand extends FactionSubCommand {

    @Subcommand("create")
    public void onCreateCommand(Player player, String factionName) {
        FactionPlayer fplayer = factionSystem.getFactionPlayer(player);
        // Check if the player is already in a faction
        if (factionSystem.getFaction(fplayer) != null) {
            player.sendMessage("You are already in a faction.");
            return;
        }

        // Check if the faction name already exists
        if (factionSystem.doesFactionExist(factionName)) {
            player.sendMessage("A faction with that name already exists.");
            return;
        }

        // Validate the faction name
        if (!(factionSystem.isValidFactionName(factionName))) {
            player.sendMessage("Invalid faction name.");
            return;
        }

        Faction createdFaction = factionSystem.createFaction(fplayer, factionName);
        player.sendMessage("Faction " + createdFaction.getName() + " has been created!");
    }
}
