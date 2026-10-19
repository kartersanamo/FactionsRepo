package commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PointsSubCommand extends FactionSubCommand {
    public PointsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("points")
    public void onPoints(Player player, @Optional String factionName, @Optional Integer value) {
        if (value != null) {
            if (!player.hasPermission("factions.admin")) { msg(player, "general.no-permission"); return; }
            FactionResult result = factionSystem.setPoints(factionSystem.getFactionByName(factionName), value);
            switch (result) {
                case SUCCESS:
                    msg(player, "points.set", "faction", factionName, "points", value);
                    break;
                case FACTION_NOT_FOUND:
                    msg(player, "general.faction-not-found");
                    break;
                default:
                    msg(player, "general.could-not");
                    break;
            }
            return;
        }
        Faction faction = factionName == null || factionName.isEmpty()
                ? factionSystem.getFaction(player.getUniqueId())
                : factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        msg(player, "points.info", "faction", faction.getName(), "points", faction.getPoints());
    }
}
