package commands.faction.economy;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class TNTSubCommand extends FactionSubCommand {
    public TNTSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("tnt")
    public void onTnt(Player player, @Optional String action, @Optional Integer amount) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        if (action == null || action.isEmpty()) {
            msg(player, "tnt.balance", "amount", faction.getTnt());
            return;
        }
        if (amount == null) { msg(player, "tnt.usage"); return; }
        if (action.equalsIgnoreCase("add")) {
            FactionResult result = factionSystem.addTnt(faction, amount);
            switch (result) {
                case SUCCESS:
                    msg(player, "tnt.added", "amount", amount, "total", faction.getTnt());
                    break;
                case INVALID_AMOUNT:
                    msg(player, "tnt.usage");
                    break;
                default:
                    msg(player, "general.could-not");
                    break;
            }
            return;
        }
        if (action.equalsIgnoreCase("take")) {
            FactionResult result = factionSystem.removeTnt(faction, amount);
            switch (result) {
                case SUCCESS:
                    msg(player, "tnt.took", "amount", amount, "total", faction.getTnt());
                    break;
                case NOT_ENOUGH:
                    msg(player, "tnt.not-enough");
                    break;
                case INVALID_AMOUNT:
                    msg(player, "tnt.usage");
                    break;
                default:
                    msg(player, "general.could-not");
                    break;
            }
            return;
        }
        msg(player, "tnt.usage");
    }
}
