package systems;

import event.claim.LandClaimEvent;
import event.claim.LandUnclaimEvent;
import event.faction.FactionCreateEvent;
import event.faction.FactionDisbandEvent;
import event.player.FactionPlayerJoinEvent;
import event.player.FactionPlayerLeaveEvent;
import model.ChunkClaim;
import model.Faction;
import model.FactionPlayer;
import model.FactionRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class FactionSystem {
    private final FactionStorage storage;
    private final Logger logger;

    private Map<UUID, Faction> factions = new HashMap<>();
    private Map<UUID, UUID> playerToFaction = new HashMap<>();
    private Map<String, UUID> claims = new HashMap<>();
    private Map<UUID, Set<UUID>> invites = new HashMap<>();

    public FactionSystem(File dataFolder, Logger logger) {
        this.storage = new FactionStorage(dataFolder, logger);
        this.logger = logger;
    }

    public void load() {
        FactionStorage.LoadedData data = storage.load();
        this.factions = data.factions;
        this.playerToFaction = data.playerToFaction;
        this.claims = data.claims;
        this.invites = data.invites;
        logger.info("Loaded " + factions.size() + " factions.");
    }

    public void save() {
        try {
            storage.save(factions, invites);
        } catch (IOException exception) {
            logger.severe("Failed to save factions: " + exception.getMessage());
        }
    }

    public FactionPlayer getFactionPlayer(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction != null) {
            FactionPlayer existing = faction.getMember(player.getUniqueId());
            if (existing != null) {
                existing.setName(player.getName());
                return existing;
            }
        }
        return new FactionPlayer(player, FactionRole.MEMBER);
    }

    public Faction getFaction(UUID playerId) {
        UUID factionId = playerToFaction.get(playerId);
        if (factionId == null) {
            return null;
        }
        return factions.get(factionId);
    }

    public Faction getFaction(FactionPlayer fplayer) {
        return getFaction(fplayer.getUniqueId());
    }

    public Faction getFactionByName(String factionName) {
        for (Faction faction : factions.values()) {
            if (faction.getName().equalsIgnoreCase(factionName)) {
                return faction;
            }
        }
        return null;
    }

    public Faction getFactionAt(ChunkClaim claim) {
        UUID factionId = claims.get(claim.toKey());
        if (factionId == null) {
            return null;
        }
        return factions.get(factionId);
    }

    public Faction findFactionByNameOrPlayer(String input) {
        Faction byName = getFactionByName(input);
        if (byName != null) {
            return byName;
        }

        Player online = Bukkit.getPlayerExact(input);
        if (online != null) {
            return getFaction(online.getUniqueId());
        }

        for (Faction faction : factions.values()) {
            for (FactionPlayer member : faction.getMembers()) {
                if (member.getName().equalsIgnoreCase(input)) {
                    return faction;
                }
            }
        }
        return null;
    }

    public boolean doesFactionExist(String factionName) {
        return getFactionByName(factionName) != null;
    }

    public boolean isValidFactionName(String factionName) {
        if (factionName == null || factionName.isEmpty()) {
            return false;
        }
        if (factionName.length() < 3 || factionName.length() > 16) {
            return false;
        }
        if (factionName.contains(" ")) {
            return false;
        }
        for (char character : factionName.toCharArray()) {
            if (!Character.isLetterOrDigit(character)) {
                return false;
            }
        }
        return true;
    }

    public Faction createFaction(Player player, String factionName) {
        FactionPlayer owner = new FactionPlayer(player, FactionRole.LEADER);
        UUID factionId = UUID.randomUUID();
        Faction faction = new Faction(factionId, owner, factionName);
        factions.put(factionId, faction);
        playerToFaction.put(player.getUniqueId(), factionId);
        Bukkit.getPluginManager().callEvent(new FactionCreateEvent(faction));
        save();
        return faction;
    }

    public boolean leaveFaction(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null) {
            return false;
        }
        if (member.isLeader()) {
            return false;
        }

        faction.removeMember(player.getUniqueId());
        playerToFaction.remove(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, member));
        save();
        return true;
    }

    public boolean disbandFaction(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            return false;
        }

        for (String claimKey : new HashSet<>(faction.getClaims())) {
            claims.remove(claimKey);
        }
        for (FactionPlayer factionMember : new HashSet<>(faction.getMembers())) {
            playerToFaction.remove(factionMember.getUniqueId());
            clearInvitesToFaction(factionMember.getUniqueId(), faction.getId());
        }
        clearAllInvitesForFaction(faction.getId());
        factions.remove(faction.getId());
        Bukkit.getPluginManager().callEvent(new FactionDisbandEvent(faction));
        save();
        return true;
    }

    public boolean invitePlayer(Player inviter, Player target) {
        Faction faction = getFaction(inviter.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer inviterMember = faction.getMember(inviter.getUniqueId());
        if (inviterMember == null || !inviterMember.isLeader()) {
            return false;
        }
        if (getFaction(target.getUniqueId()) != null) {
            return false;
        }

        invites.computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        save();
        return true;
    }

    public boolean joinFaction(Player player, String factionName) {
        if (getFaction(player.getUniqueId()) != null) {
            return false;
        }
        Faction faction = getFactionByName(factionName);
        if (faction == null) {
            return false;
        }
        Set<UUID> playerInvites = invites.getOrDefault(player.getUniqueId(), Collections.emptySet());
        if (!playerInvites.contains(faction.getId())) {
            return false;
        }

        FactionPlayer member = new FactionPlayer(player, FactionRole.MEMBER);
        faction.addMember(member);
        playerToFaction.put(player.getUniqueId(), faction.getId());
        playerInvites.remove(faction.getId());
        if (playerInvites.isEmpty()) {
            invites.remove(player.getUniqueId());
        }
        Bukkit.getPluginManager().callEvent(new FactionPlayerJoinEvent(faction, member));
        save();
        return true;
    }

    public boolean kickPlayer(Player kicker, String targetName) {
        Faction faction = getFaction(kicker.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer kickerMember = faction.getMember(kicker.getUniqueId());
        if (kickerMember == null || !kickerMember.isLeader()) {
            return false;
        }

        FactionPlayer target = null;
        for (FactionPlayer member : faction.getMembers()) {
            if (member.getName().equalsIgnoreCase(targetName)) {
                target = member;
                break;
            }
        }
        if (target == null || target.isLeader()) {
            return false;
        }

        faction.removeMember(target.getUniqueId());
        playerToFaction.remove(target.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, target));
        save();
        return true;
    }

    public boolean claimChunk(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        if (claims.containsKey(claim.toKey())) {
            return false;
        }

        claims.put(claim.toKey(), faction.getId());
        faction.getClaims().add(claim.toKey());
        Bukkit.getPluginManager().callEvent(new LandClaimEvent(faction, claim));
        save();
        return true;
    }

    public boolean unclaimChunk(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        UUID ownerId = claims.get(claim.toKey());
        if (ownerId == null || !ownerId.equals(faction.getId())) {
            return false;
        }

        claims.remove(claim.toKey());
        faction.getClaims().remove(claim.toKey());
        Bukkit.getPluginManager().callEvent(new LandUnclaimEvent(faction, claim));
        save();
        return true;
    }

    public boolean canBuild(Player player, ChunkClaim claim) {
        UUID ownerId = claims.get(claim.toKey());
        if (ownerId == null) {
            return true;
        }
        UUID playerFactionId = playerToFaction.get(player.getUniqueId());
        return ownerId.equals(playerFactionId);
    }

    public Set<UUID> getInvites(UUID playerId) {
        return invites.getOrDefault(playerId, Collections.emptySet());
    }

    private void clearAllInvitesForFaction(UUID factionId) {
        for (Set<UUID> factionIds : invites.values()) {
            factionIds.remove(factionId);
        }
        invites.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void clearInvitesToFaction(UUID playerId, UUID factionId) {
        Set<UUID> playerInvites = invites.get(playerId);
        if (playerInvites == null) {
            return;
        }
        playerInvites.remove(factionId);
        if (playerInvites.isEmpty()) {
            invites.remove(playerId);
        }
    }
}
