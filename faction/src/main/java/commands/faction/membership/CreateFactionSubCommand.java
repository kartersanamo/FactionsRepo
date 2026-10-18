package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class CreateFactionSubCommand extends FactionSubCommand {

    public CreateFactionSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("create")
    public void onCreateCommand(Player player, String factionName) {
        if (factionSystem.getFaction(player.getUniqueId()) != null) {
            msg(player, "general.already-in-faction");
            return;
        }
        if (factionSystem.doesFactionExist(factionName)) {
            msg(player, "create.exists");
            return;
        }
        if (!factionSystem.isValidFactionName(factionName)) {
            msg(player, "create.invalid-name");
            return;
        }

        Faction createdFaction = factionSystem.createFaction(player, factionName);
        msg(player, "create.success", "faction", createdFaction.getName());
    }
}
