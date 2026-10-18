package commands.faction.economy;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import systems.FactionSystem;

public class VaultSubCommand extends FactionSubCommand {
    public VaultSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("vault")
    public void onVault(Player player, @Default("1") Integer number) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        int vaultNumber = number == null ? 1 : number;
        if (vaultNumber < 1 || vaultNumber > faction.getMaxVaults()) {
            player.sendMessage("Vault must be between 1 and " + faction.getMaxVaults() + ".");
            return;
        }
        Inventory inventory = Bukkit.createInventory(player, 27, "Faction Vault #" + vaultNumber);
        player.openInventory(inventory);
        player.sendMessage("Opened temporary vault #" + vaultNumber + " (not persisted yet).");
    }
}
