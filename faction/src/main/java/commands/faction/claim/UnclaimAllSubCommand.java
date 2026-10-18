package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class UnclaimAllSubCommand extends FactionSubCommand {
    public UnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("unclaimall")
    public void onUnclaimAll(Player player) {
        if (!factionSystem.unclaimAll(player)) {
            player.sendMessage("Could not unclaim all land.");
            return;
        }
        player.sendMessage("Unclaimed all faction land.");
    }
}
