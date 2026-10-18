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
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        if (action == null || action.isEmpty() || action.equalsIgnoreCase("bal")) {
            msg(player, "money.balance", "amount", String.format("%.2f", faction.getMoney()));
            return;
        }
        if (amount == null || amount <= 0) { msg(player, "money.usage"); return; }
        if (action.equalsIgnoreCase("deposit")) {
            if (!factionSystem.depositMoney(player, amount)) { msg(player, "money.deposit-failed"); return; }
            msg(player, "money.deposited", "amount", amount);
            return;
        }
        if (action.equalsIgnoreCase("withdraw")) {
            if (!factionSystem.withdrawMoney(player, amount)) { msg(player, "money.withdraw-failed"); return; }
            msg(player, "money.withdrew", "amount", amount);
            return;
        }
        msg(player, "money.usage");
    }
}
