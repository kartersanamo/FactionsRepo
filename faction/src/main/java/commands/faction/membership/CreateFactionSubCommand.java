package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class CreateFactionSubCommand extends FactionSubCommand {

    public CreateFactionSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("create")
    public void onCreateCommand(Player player, String factionName) {
        FactionResult result = factionSystem.createFaction(player, factionName);
        switch (result) {
            case SUCCESS:
                msg(player, "create.success", "faction", factionName);
                break;
            case ALREADY_IN_FACTION:
                msg(player, "general.already-in-faction");
                break;
            case NAME_TAKEN:
                msg(player, "create.exists");
                break;
            case INVALID_NAME:
                msg(player, "create.invalid-name");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
