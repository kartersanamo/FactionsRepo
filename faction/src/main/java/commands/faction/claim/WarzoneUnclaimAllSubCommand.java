package commands.faction.claim;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class WarzoneUnclaimAllSubCommand extends FactionSubCommand {
    public WarzoneUnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("warzoneunclaimall")
    @CommandPermission("factions.admin")
    public void onWarzoneUnclaimAll(Player player) {
        Faction faction = factionSystem.getFactionByName("Warzone");
        FactionResult result = factionSystem.unclaimAll(faction);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.warzone-unclaim");
                break;
            case FACTION_NOT_FOUND:
                msg(player, "claim.warzone-missing");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
