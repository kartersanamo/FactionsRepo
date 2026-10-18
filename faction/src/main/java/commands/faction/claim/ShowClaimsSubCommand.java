package commands.faction.claim;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ShowClaimsSubCommand extends FactionSubCommand {
    public ShowClaimsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("showclaims|claims")
    public void onShowClaims(Player player, @Optional String factionName) {
        Faction faction = factionName == null || factionName.isEmpty()
                ? factionSystem.getFaction(player.getUniqueId())
                : factionSystem.getFactionByName(factionName);
        if (faction == null) { player.sendMessage("Faction not found."); return; }
        if (faction.getClaims().isEmpty()) { player.sendMessage("No claims."); return; }
        player.sendMessage("Claims for " + faction.getName() + " (" + faction.getLand() + "):");
        int shown = 0;
        for (String key : faction.getClaims()) {
            ChunkClaim claim = ChunkClaim.fromKey(key);
            player.sendMessage("- " + claim.getWorldName() + " " + claim.getX() + ", " + claim.getZ());
            shown++;
            if (shown >= 25) { player.sendMessage("...and more"); break; }
        }
    }
}
