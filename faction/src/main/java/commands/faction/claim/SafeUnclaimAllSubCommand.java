package commands.faction.claim;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SafeUnclaimAllSubCommand extends FactionSubCommand {
    public SafeUnclaimAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("safeunclaimall")
    @CommandPermission("factions.admin")
    public void onSafeUnclaimAll(Player player) {
        Faction faction = factionSystem.getFactionByName("Safezone");
        if (faction == null) { msg(player, "claim.safe-missing"); return; }
        factionSystem.unclaimAll(faction);
        msg(player, "claim.safe-unclaim");
    }
}
