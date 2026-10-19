package commands.faction.economy;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
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
            msg(player, "general.not-in-faction");
            return;
        }
        if (amount <= 0) {
            msg(player, "tnt.fill-invalid");
            return;
        }
        Block target = player.getTargetBlock(new HashSet<Byte>(), 5);
        if (target == null || target.getType() != Material.DISPENSER) {
            msg(player, "tnt.fill-look");
            return;
        }
        FactionResult result = factionSystem.removeTnt(faction, amount);
        switch (result) {
            case SUCCESS:
                Dispenser dispenser = (Dispenser) target.getState();
                dispenser.getInventory().addItem(new ItemStack(Material.TNT, amount));
                msg(player, "tnt.fill-success", "amount", amount);
                break;
            case NOT_ENOUGH:
                msg(player, "tnt.not-enough");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
