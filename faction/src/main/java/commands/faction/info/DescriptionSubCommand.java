package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DescriptionSubCommand extends FactionSubCommand {
    public DescriptionSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("desc|description")
    public void onDescription(Player player, String description) {
        FactionResult result = factionSystem.setDescription(player, description);
        switch (result) {
            case SUCCESS:
                msg(player, "description.success");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            default:
                msg(player, "description.failed");
                break;
        }
    }
}
