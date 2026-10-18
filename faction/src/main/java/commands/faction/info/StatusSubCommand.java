package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class StatusSubCommand extends FactionSubCommand {
    public StatusSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("status")
    public void onStatus(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction at = factionSystem.getFactionAt(claim);
        String wilderness = messages.raw("general.wilderness");
        msg(player, "status.you", "faction", faction == null ? wilderness : faction.getName());
        msg(player, "status.chunk", "chunk", claim.getX(), "chunkz", claim.getZ(),
                "owner", at == null ? wilderness : at.getName());
        msg(player, "status.chat", "mode", factionSystem.getFlags().getChatMode(player.getUniqueId()));
        msg(player, "status.autoclaim", "state", factionSystem.getFlags().hasAutoClaim(player.getUniqueId()));
        msg(player, "status.fly", "state", factionSystem.getFlags().hasFly(player.getUniqueId()));
    }
}
