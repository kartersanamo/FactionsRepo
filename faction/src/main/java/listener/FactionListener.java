package listener;

import model.ChunkClaim;
import model.Faction;
import model.FactionChatMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import systems.FactionSystem;

public class FactionListener implements Listener {
    private final FactionSystem factionSystem;

    public FactionListener(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().getX() == event.getTo().getChunk().getX()
                && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ()) {
            return;
        }

        Player player = event.getPlayer();
        ChunkClaim toClaim = ChunkClaim.from(event.getTo());

        if (factionSystem.getFlags().hasAutoClaim(player.getUniqueId())) {
            factionSystem.claimChunk(player, toClaim);
        }

        if (factionSystem.getFlags().hasFly(player.getUniqueId())) {
            Faction faction = factionSystem.getFaction(player.getUniqueId());
            Faction at = factionSystem.getFactionAt(toClaim);
            boolean onOwnClaim = faction != null && at != null && faction.getId().equals(at.getId());
            if (!onOwnClaim) {
                factionSystem.getFlags().setFly(player.getUniqueId(), false);
                player.setAllowFlight(false);
                player.setFlying(false);
                factionSystem.getMessages().send(player, "fly.disabled-territory");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        FactionChatMode mode = factionSystem.getFlags().getChatMode(player.getUniqueId());
        if (mode == FactionChatMode.PUBLIC) {
            notifyChatSpies(player, event.getMessage());
            return;
        }

        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            factionSystem.getFlags().setChatMode(player.getUniqueId(), FactionChatMode.PUBLIC);
            return;
        }

        event.setCancelled(true);
        Object[] placeholders = {"faction", faction.getName(), "player", player.getName(),
                "message", event.getMessage()};

        if (mode == FactionChatMode.FACTION) {
            factionSystem.broadcastToFaction(faction, "chat.format-faction", placeholders);
        } else if (mode == FactionChatMode.ALLY) {
            factionSystem.broadcastToAllies(faction, "chat.format-ally", placeholders);
        }
        notifyChatSpies(player, event.getMessage());
    }

    private void notifyChatSpies(Player sender, String message) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(sender.getUniqueId())) {
                continue;
            }
            if (factionSystem.getFlags().hasChatSpy(online.getUniqueId())) {
                factionSystem.getMessages().send(online, "chat.spy",
                        "player", sender.getName(), "message", message);
            }
        }
    }
}
