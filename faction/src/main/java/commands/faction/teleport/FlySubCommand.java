package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.ChunkClaim;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class FlySubCommand extends FactionSubCommand {
    public FlySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("fly")
    public void onFly(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        Faction at = factionSystem.getFactionAt(ChunkClaim.from(player.getLocation()));
        if (at == null || !at.getId().equals(faction.getId())) {
            player.sendMessage("You can only fly in your own claims.");
            return;
        }
        boolean enabled = factionSystem.getFlags().toggleFly(player.getUniqueId());
        player.setAllowFlight(enabled);
        player.setFlying(enabled);
        player.sendMessage("Faction fly " + (enabled ? "enabled" : "disabled") + ".");
    }
}
