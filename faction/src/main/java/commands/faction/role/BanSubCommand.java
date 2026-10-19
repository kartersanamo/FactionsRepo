package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class BanSubCommand extends FactionSubCommand {
    public BanSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ban")
    @CommandCompletion("@players")
    public void onBan(Player player, String target) {
        FactionResult result = factionSystem.banPlayer(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "ban.success", "player", target);
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
                msg(player, "ban.failed");
                break;
        }
    }
}
