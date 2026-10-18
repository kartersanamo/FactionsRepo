package commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class UpgradesSubCommand extends FactionSubCommand {
    public UpgradesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("upgrades")
    public void onUpgrades(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        player.sendMessage("Upgrades for " + faction.getName() + ":");
        player.sendMessage("- Max vaults: " + faction.getMaxVaults());
        player.sendMessage("- Power boost: +" + faction.getPowerBoost());
    }
}
