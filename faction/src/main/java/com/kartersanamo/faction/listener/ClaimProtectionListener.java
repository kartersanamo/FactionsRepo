package com.kartersanamo.faction.listener;

import com.kartersanamo.faction.model.ChunkClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.kartersanamo.faction.systems.FactionSystem;

public class ClaimProtectionListener implements Listener {
    private final FactionSystem factionSystem;

    public ClaimProtectionListener(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            factionSystem.getMessages().send(event.getPlayer(), "claim.protected");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            factionSystem.getMessages().send(event.getPlayer(), "claim.protected");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!canBuild(event.getPlayer(), ChunkClaim.from(event.getClickedBlock().getLocation()))) {
            event.setCancelled(true);
            factionSystem.getMessages().send(event.getPlayer(), "claim.protected");
        }
    }

    private boolean canBuild(Player player, ChunkClaim claim) {
        if (factionSystem.getFlags().hasBypass(player.getUniqueId())) {
            return true;
        }
        return factionSystem.canBuild(player, claim);
    }
}
