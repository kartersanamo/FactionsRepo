package commands.faction.claim;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SafeUnclaimAllSubCommand extends FactionSubCommand {
    public SafeUnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("safeunclaimall")
    @CommandPermission("factions.admin")
    public void onSafeUnclaimAll(Player player) {
        Faction faction = factionSystem.getFactionByName("Safezone");
        FactionResult result = factionSystem.unclaimAll(faction);
        switch (result) {
            case SUCCESS:
                msg(player, "claim.safe-unclaim");
                break;
            case FACTION_NOT_FOUND:
                msg(player, "claim.safe-missing");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
