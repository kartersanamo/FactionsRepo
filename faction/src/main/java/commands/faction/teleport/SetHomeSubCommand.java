package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetHomeSubCommand extends FactionSubCommand {
    public SetHomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("sethome")
    public void onSetHome(Player player) {
        if (!factionSystem.setHome(player)) {
            player.sendMessage("Could not set home. Need permission and stand in your land.");
            return;
        }
        player.sendMessage("Faction home set.");
    }
}
