package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TitleSubCommand extends FactionSubCommand {
    public TitleSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("title")
    @CommandCompletion("@players")
    public void onTitle(Player player, String target, String title) {
        if (!factionSystem.setTitle(player, target, title)) {
            msg(player, "title.failed");
            return;
        }
        msg(player, "title.success", "player", target, "title", title);
    }
}
