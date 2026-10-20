package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.kartersanamo.faction.systems.FactionSystem;

public class ViewChestSubCommand extends FactionSubCommand {
    public ViewChestSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("viewchest")
    @CommandPermission("factions.admin")
    public void onViewChest(Player player, String factionName) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        Inventory inventory = Bukkit.createInventory(player, 54, "View Chest: " + faction.getName());
        player.openInventory(inventory);
        msg(player, "vault.view-opened", "faction", factionName);
    }
}
