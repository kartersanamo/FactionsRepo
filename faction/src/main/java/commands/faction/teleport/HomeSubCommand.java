package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class HomeSubCommand extends FactionSubCommand {
    public HomeSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("home")
    public void onHome(Player player) {
        if (!factionSystem.teleportHome(player)) {
            msg(player, "home.teleport-failed");
            return;
        }
        msg(player, "home.teleport");
    }
}
