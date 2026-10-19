package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DeleteHomeSubCommand extends FactionSubCommand {
    public DeleteHomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("delhome|deletehome")
    public void onDeleteHome(Player player) {
        FactionResult result = factionSystem.deleteHome(player);
        switch (result) {
            case SUCCESS:
                msg(player, "home.deleted");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            default:
                msg(player, "home.delete-failed");
                break;
        }
    }
}
