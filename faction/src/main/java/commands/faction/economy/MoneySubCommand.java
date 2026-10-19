package commands.faction.economy;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
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
        if (amount == null) { msg(player, "money.usage"); return; }
        if (action.equalsIgnoreCase("deposit")) {
            deposit(player, amount);
            return;
        }
        if (action.equalsIgnoreCase("withdraw")) {
            withdraw(player, amount);
            return;
        }
        msg(player, "money.usage");
    }

    private void deposit(Player player, double amount) {
        FactionResult result = factionSystem.depositMoney(player, amount);
        switch (result) {
            case SUCCESS:
                msg(player, "money.deposited", "amount", amount);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case INVALID_AMOUNT:
                msg(player, "money.usage");
                break;
            default:
                msg(player, "money.deposit-failed");
                break;
        }
    }

    private void withdraw(Player player, double amount) {
        FactionResult result = factionSystem.withdrawMoney(player, amount);
        switch (result) {
            case SUCCESS:
                msg(player, "money.withdrew", "amount", amount);
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NO_PERMISSION:
                msg(player, "general.no-permission");
                break;
            case INVALID_AMOUNT:
                msg(player, "money.usage");
                break;
            case NOT_ENOUGH:
                msg(player, "money.withdraw-failed");
                break;
            default:
                msg(player, "money.withdraw-failed");
                break;
        }
    }
}
