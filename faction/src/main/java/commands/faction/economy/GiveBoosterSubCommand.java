package commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class GiveBoosterSubCommand extends FactionSubCommand {
    public GiveBoosterSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("givebooster")
    @CommandPermission("factions.admin")
    public void onGiveBooster(Player player, String factionName, String boosterType) {
        if (factionSystem.getFactionByName(factionName) == null) {
            player.sendMessage("Faction not found.");
            return;
        }
        player.sendMessage("Granted booster '" + boosterType + "' to " + factionName + " (placeholder).");
    }
}
