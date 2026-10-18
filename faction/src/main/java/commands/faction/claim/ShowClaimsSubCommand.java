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
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        if (faction.getClaims().isEmpty()) { msg(player, "claim.show-none"); return; }
        msg(player, "claim.show-header", "faction", faction.getName(), "amount", faction.getLand());
        int shown = 0;
        for (String key : faction.getClaims()) {
            ChunkClaim claim = ChunkClaim.fromKey(key);
            msg(player, "claim.show-line", "world", claim.getWorldName(), "chunk", claim.getX(), "chunkz", claim.getZ());
            shown++;
            if (shown >= 25) { msg(player, "claim.show-more"); break; }
        }
    }
}
