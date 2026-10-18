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
            msg(player, "general.not-in-faction");
            return;
        }
        boolean enabled = factionSystem.getFlags().toggleAutoClaim(player.getUniqueId());
        msg(player, enabled ? "claim.autoclaim-on" : "claim.autoclaim-off");
    }
}
