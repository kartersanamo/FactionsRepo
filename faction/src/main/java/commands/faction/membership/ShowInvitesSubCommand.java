package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

import java.util.List;

public class ShowInvitesSubCommand extends FactionSubCommand {
    public ShowInvitesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("invites|showinvites")
    public void onShowInvites(Player player) {
        List<String> invites = factionSystem.getInviteFactionNames(player);
        if (invites.isEmpty()) { player.sendMessage("You have no invites."); return; }
        player.sendMessage("Invites: " + String.join(", ", invites));
    }
}
