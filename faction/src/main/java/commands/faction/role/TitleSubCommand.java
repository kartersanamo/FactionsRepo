package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TitleSubCommand extends FactionSubCommand {
    public TitleSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("title")
    @CommandCompletion("@players")
    public void onTitle(Player player, String target, String title) {
        FactionResult result = factionSystem.setTitle(player, target, title);
        switch (result) {
            case SUCCESS:
                msg(player, "title.success", "player", target, "title", title);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case NOT_MEMBER:
                msg(player, "kick.not-member");
                break;
            default:
                msg(player, "title.failed");
                break;
        }
    }
}
