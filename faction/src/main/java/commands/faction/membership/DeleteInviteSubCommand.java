package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DeleteInviteSubCommand extends FactionSubCommand {
    public DeleteInviteSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("deinvite|deleteinvite")
    public void onDeleteInvite(Player player, String factionName) {
        FactionResult result = factionSystem.deleteInvite(player, factionName);
        switch (result) {
            case SUCCESS:
                msg(player, "invites.deleted", "faction", factionName);
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            case INVITE_MISSING:
                msg(player, "invites.missing");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
