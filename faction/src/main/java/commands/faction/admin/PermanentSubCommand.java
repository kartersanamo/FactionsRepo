package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PermanentSubCommand extends FactionSubCommand {
    public PermanentSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("permanent")
    @CommandPermission("factions.admin")
    public void onPermanent(Player player, String factionName) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        boolean next = !faction.isPermanent();
        FactionResult result = factionSystem.setPermanent(faction, next);
        switch (result) {
            case SUCCESS:
                msg(player, "admin.permanent", "faction", factionName, "state", next);
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
