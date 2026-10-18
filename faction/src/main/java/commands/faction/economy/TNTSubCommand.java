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
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        if (action == null || action.isEmpty()) {
            msg(player, "tnt.balance", "amount", faction.getTnt());
            return;
        }
        if (amount == null || amount <= 0) { msg(player, "tnt.usage"); return; }
        if (action.equalsIgnoreCase("add")) {
            factionSystem.addTnt(faction, amount);
            msg(player, "tnt.added", "amount", amount, "total", faction.getTnt());
            return;
        }
        if (action.equalsIgnoreCase("take")) {
            if (!factionSystem.removeTnt(faction, amount)) { msg(player, "tnt.not-enough"); return; }
            msg(player, "tnt.took", "amount", amount, "total", faction.getTnt());
            return;
        }
        msg(player, "tnt.usage");
    }
}
