package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DeleteInviteSubCommand extends FactionSubCommand {
    public DeleteInviteSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("deinvite|deleteinvite")
    public void onDeleteInvite(Player player, String factionName) {
        if (!factionSystem.deleteInvite(player, factionName)) {
            player.sendMessage("No invite from that faction.");
            return;
        }
        player.sendMessage("Deleted invite from " + factionName + ".");
    }
}
