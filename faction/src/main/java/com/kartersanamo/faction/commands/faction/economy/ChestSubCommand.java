package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.kartersanamo.faction.systems.FactionSystem;

public class ChestSubCommand extends FactionSubCommand {
    public ChestSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("chest")
    public void onChest(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        Inventory inventory = Bukkit.createInventory(player, 54, "Faction Chest");
        player.openInventory(inventory);
        msg(player, "vault.chest-opened");
    }
}
