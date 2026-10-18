package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class DebugSubCommand extends FactionSubCommand {
    public DebugSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("debug")
    @CommandPermission("factions.admin")
    public void onDebug(Player player) {
        player.sendMessage("Factions: " + factionSystem.getAllFactions().size());
        player.sendMessage("Your faction: " + (factionSystem.getFaction(player.getUniqueId()) == null
                ? "none" : factionSystem.getFaction(player.getUniqueId()).getName()));
        player.sendMessage("Bypass: " + factionSystem.getFlags().hasBypass(player.getUniqueId()));
        player.sendMessage("Autoclaim: " + factionSystem.getFlags().hasAutoClaim(player.getUniqueId()));
    }
}
