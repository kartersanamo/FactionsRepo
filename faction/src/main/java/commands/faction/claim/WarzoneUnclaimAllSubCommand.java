package commands.faction.claim;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class WarzoneUnclaimAllSubCommand extends FactionSubCommand {
    public WarzoneUnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("warzoneunclaimall")
    @CommandPermission("factions.admin")
    public void onWarzoneUnclaimAll(Player player) {
        Faction faction = factionSystem.getFactionByName("Warzone");
        if (faction == null) { msg(player, "claim.warzone-missing"); return; }
        factionSystem.unclaimAll(faction);
        msg(player, "claim.warzone-unclaim");
    }
}
