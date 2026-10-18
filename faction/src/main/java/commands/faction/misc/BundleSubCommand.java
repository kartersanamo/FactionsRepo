package commands.faction.misc;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class BundleSubCommand extends FactionSubCommand {
    public BundleSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("bundle")
    public void onBundle(Player player) {
        player.sendMessage("Faction bundles are not configured yet.");
    }
}
