package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class AutoClaimSubCommand extends FactionSubCommand {
    public AutoClaimSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("autoclaim")
    public void onAutoClaim(Player player) {
        if (factionSystem.getFaction(player.getUniqueId()) == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        boolean enabled = factionSystem.getFlags().toggleAutoClaim(player.getUniqueId());
        player.sendMessage("Autoclaim " + (enabled ? "enabled" : "disabled") + ".");
    }
}
