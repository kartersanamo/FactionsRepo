package commands.faction.economy;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TNTSubCommand extends FactionSubCommand {
    public TNTSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("tnt")
    public void onTnt(Player player, @Optional String action, @Optional Integer amount) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        if (action == null || action.isEmpty()) {
            player.sendMessage("Faction TNT bank: " + faction.getTnt());
            return;
        }
        if (amount == null || amount <= 0) { player.sendMessage("Usage: /f tnt <add|take> <amount>"); return; }
        if (action.equalsIgnoreCase("add")) {
            factionSystem.addTnt(faction, amount);
            player.sendMessage("Added " + amount + " TNT. Bank: " + faction.getTnt());
            return;
        }
        if (action.equalsIgnoreCase("take")) {
            if (!factionSystem.removeTnt(faction, amount)) { player.sendMessage("Not enough TNT."); return; }
            player.sendMessage("Took " + amount + " TNT. Bank: " + faction.getTnt());
            return;
        }
        player.sendMessage("Usage: /f tnt [add|take] [amount]");
    }
}
