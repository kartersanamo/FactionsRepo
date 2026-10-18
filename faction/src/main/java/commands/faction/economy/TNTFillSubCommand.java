package commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import systems.FactionSystem;

import java.util.HashSet;

public class TNTFillSubCommand extends FactionSubCommand {
    public TNTFillSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("tntfill")
    public void onTntFill(Player player, int amount) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        Block target = player.getTargetBlock(new HashSet<Byte>(), 5);
        if (target == null || target.getType() != Material.DISPENSER) {
            player.sendMessage("Look at a dispenser.");
            return;
        }
        if (!factionSystem.removeTnt(faction, amount)) {
            player.sendMessage("Not enough TNT in the bank.");
            return;
        }
        Dispenser dispenser = (Dispenser) target.getState();
        dispenser.getInventory().addItem(new ItemStack(Material.TNT, amount));
        player.sendMessage("Filled dispenser with " + amount + " TNT.");
    }
}
