package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ReloadSubCommand extends FactionSubCommand {
    public ReloadSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("reload")
    @CommandPermission("factions.admin")
    public void onReload(Player player) {
        factionSystem.save();
        factionSystem.load();
        player.sendMessage("Factions reloaded from disk.");
    }
}
