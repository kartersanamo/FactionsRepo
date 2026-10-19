package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TagSubCommand extends FactionSubCommand {
    public TagSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("tag|rename")
    public void onTag(Player player, String newName) {
        FactionResult result = factionSystem.setTag(player, newName);
        switch (result) {
            case SUCCESS:
                msg(player, "tag.success", "faction", newName);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            case INVALID_NAME:
                msg(player, "create.invalid-name");
                break;
            case NAME_TAKEN:
                msg(player, "create.exists");
                break;
            default:
                msg(player, "tag.failed");
                break;
        }
    }
}
