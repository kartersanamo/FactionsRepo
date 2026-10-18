package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TagSubCommand extends FactionSubCommand {
    public TagSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("tag|rename")
    public void onTag(Player player, String newName) {
        if (!factionSystem.setTag(player, newName)) {
            msg(player, "tag.failed");
            return;
        }
        msg(player, "tag.success", "faction", newName);
    }
}
