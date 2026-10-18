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
        player.sendMessage("You: " + (faction == null ? "Wilderness" : faction.getName()));
        player.sendMessage("Chunk " + claim.getX() + ", " + claim.getZ() + ": "
                + (at == null ? "Wilderness" : at.getName()));
        player.sendMessage("Chat: " + factionSystem.getFlags().getChatMode(player.getUniqueId()));
        player.sendMessage("Autoclaim: " + factionSystem.getFlags().hasAutoClaim(player.getUniqueId()));
        player.sendMessage("Fly: " + factionSystem.getFlags().hasFly(player.getUniqueId()));
    }
}
