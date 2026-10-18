package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DescriptionSubCommand extends FactionSubCommand {
    public DescriptionSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("desc|description")
    public void onDescription(Player player, String description) {
        if (!factionSystem.setDescription(player, description)) {
            msg(player, "description.failed");
            return;
        }
        msg(player, "description.success");
    }
}
