package commands.faction.economy;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class MoneySubCommand extends FactionSubCommand {
    public MoneySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("money|balance|bal")
    public void onMoney(Player player, @Optional String action, @Optional Double amount) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        if (action == null || action.isEmpty() || action.equalsIgnoreCase("bal")) {
            player.sendMessage("Faction balance: $" + String.format("%.2f", faction.getMoney()));
            return;
        }
        if (amount == null || amount <= 0) { player.sendMessage("Usage: /f money <deposit|withdraw> <amount>"); return; }
        if (action.equalsIgnoreCase("deposit")) {
            if (!factionSystem.depositMoney(player, amount)) { player.sendMessage("Deposit failed."); return; }
            player.sendMessage("Deposited $" + amount + ".");
            return;
        }
        if (action.equalsIgnoreCase("withdraw")) {
            if (!factionSystem.withdrawMoney(player, amount)) { player.sendMessage("Withdraw failed."); return; }
            player.sendMessage("Withdrew $" + amount + ".");
            return;
        }
        player.sendMessage("Usage: /f money [deposit|withdraw] [amount]");
    }
}
