package commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class BoostersSubCommand extends FactionSubCommand {
    public BoostersSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("boosters")
    public void onBoosters(Player player) {
        player.sendMessage("No active boosters.");
    }
}
