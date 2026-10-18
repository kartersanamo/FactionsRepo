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
        msg(player, "admin.debug-factions", "amount", factionSystem.getAllFactions().size());
        msg(player, "admin.debug-yours", "faction", (factionSystem.getFaction(player.getUniqueId()) == null
                ? "none" : factionSystem.getFaction(player.getUniqueId()).getName()));
        msg(player, "admin.debug-bypass", "state", factionSystem.getFlags().hasBypass(player.getUniqueId()));
        msg(player, "admin.debug-autoclaim", "state", factionSystem.getFlags().hasAutoClaim(player.getUniqueId()));
    }
}
