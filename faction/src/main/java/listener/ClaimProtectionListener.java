package listener;

import model.ChunkClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import systems.FactionSystem;

public class ClaimProtectionListener implements Listener {
    private final FactionSystem factionSystem;

    public ClaimProtectionListener(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You cannot build here.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You cannot build here.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getClickedBlock().getLocation()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You cannot build here.");
        }
    }

    private boolean canBuild(Player player, ChunkClaim claim) {
        return factionSystem.canBuild(player, claim);
    }
}
