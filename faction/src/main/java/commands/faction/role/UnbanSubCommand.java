package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class UnbanSubCommand extends FactionSubCommand {
    public UnbanSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("unban")
    @CommandCompletion("@players")
    public void onUnban(Player player, String target) {
        FactionResult result = factionSystem.unbanPlayer(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "unban.success", "player", target);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case PLAYER_NOT_FOUND:
                msg(player, "general.player-not-found");
                break;
            default:
                msg(player, "unban.failed");
                break;
        }
    }
}
