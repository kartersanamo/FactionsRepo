package systems;

import event.claim.LandClaimEvent;
import event.claim.LandUnclaimEvent;
import event.faction.FactionCreateEvent;
import event.faction.FactionDisbandEvent;
import event.player.FactionPlayerJoinEvent;
import event.player.FactionPlayerLeaveEvent;
import messages.Messages;
import model.ChunkClaim;
import model.Faction;
import model.FactionPlayer;
import model.FactionRole;
import model.NamedLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class FactionSystem {
    private static final int CORNER_OFFSET = 2000;

    private final FactionStorage storage;
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Messages messages;
    private final FactionPlayerFlags flags = new FactionPlayerFlags();
    private final Random random = new Random();
    private final Set<String> reservedNames = new HashSet<>();

    private Map<UUID, Faction> factions = new HashMap<>();
    private Map<UUID, UUID> playerToFaction = new HashMap<>();
    private Map<String, UUID> claims = new HashMap<>();
    private Map<UUID, Set<UUID>> invites = new HashMap<>();
    private Map<UUID, Set<UUID>> altInvites = new HashMap<>();

    public FactionSystem(JavaPlugin plugin) {
        this.plugin = plugin;
        this.storage = new FactionStorage(plugin.getDataFolder(), plugin.getLogger());
        this.logger = plugin.getLogger();
        this.messages = new Messages(plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Messages getMessages() {
        return messages;
    }

    public void load() {
        FactionStorage.LoadedData data = storage.load();
        this.factions = data.factions;
        this.playerToFaction = data.playerToFaction;
        this.claims = data.claims;
        this.invites = data.invites;
        this.altInvites = data.altInvites;
        logger.info("Loaded " + factions.size() + " factions.");
    }

    public void save() {
        try {
            storage.save(factions, invites, altInvites);
        } catch (IOException exception) {
            logger.severe("Failed to save factions: " + exception.getMessage());
        }
    }

    public FactionPlayerFlags getFlags() {
        return flags;
    }

    public boolean hasBypass(UUID playerId) {
        return flags.hasBypass(playerId);
    }

    public Collection<Faction> getAllFactions() {
        return Collections.unmodifiableCollection(factions.values());
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

    public Faction getFactionById(UUID factionId) {
        return factions.get(factionId);
    }

    public String formatFactionNames(Set<UUID> factionIds) {
        if (factionIds == null || factionIds.isEmpty()) {
            return "None";
        }
        List<String> names = new ArrayList<>();
        for (UUID factionId : factionIds) {
            Faction faction = factions.get(factionId);
            if (faction != null) {
                names.add(faction.getName());
            }
        }
        if (names.isEmpty()) {
            return "None";
        }
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return String.join(", ", names);
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

    public boolean isReserved(String name) {
        return name != null && reservedNames.contains(name.toLowerCase());
    }

    public void reserveName(String name) {
        if (name != null) {
            reservedNames.add(name.toLowerCase());
        }
    }

    public void unreserveName(String name) {
        if (name != null) {
            reservedNames.remove(name.toLowerCase());
        }
    }

    public Faction createFaction(Player player, String factionName) {
        if (getFaction(player.getUniqueId()) != null) {
            return null;
        }
        if (!isValidFactionName(factionName) || doesFactionExist(factionName) || isReserved(factionName)) {
            return null;
        }

        FactionPlayer owner = new FactionPlayer(player, FactionRole.LEADER);
        UUID factionId = UUID.randomUUID();
        Faction faction = new Faction(factionId, owner, factionName);
        faction.recalculatePower();
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
        if (member == null || member.isLeader()) {
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
        if (faction.isPermanent()) {
            return false;
        }
        return disbandFactionInternal(faction);
    }

    public boolean adminDisband(Faction faction) {
        if (faction == null) {
            return false;
        }
        return disbandFactionInternal(faction);
    }

    private boolean disbandFactionInternal(Faction faction) {
        for (String claimKey : new HashSet<>(faction.getClaims())) {
            claims.remove(claimKey);
        }
        for (FactionPlayer factionMember : new HashSet<>(faction.getMembers())) {
            playerToFaction.remove(factionMember.getUniqueId());
            clearInvitesToFaction(factionMember.getUniqueId(), faction.getId());
        }
        clearAllInvitesForFaction(faction.getId());
        removeRelationsForFaction(faction.getId());
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
        if (inviterMember == null || !inviterMember.getRole().canInvite()) {
            return false;
        }
        if (getFaction(target.getUniqueId()) != null) {
            return false;
        }
        if (faction.getBanned().contains(target.getUniqueId())) {
            return false;
        }

        clearInviteFromMap(altInvites, target.getUniqueId(), faction.getId());
        invites.computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        save();
        return true;
    }

    public boolean inviteAlt(Player inviter, Player target) {
        Faction faction = getFaction(inviter.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer inviterMember = faction.getMember(inviter.getUniqueId());
        if (inviterMember == null || !inviterMember.getRole().canInvite()) {
            return false;
        }
        if (getFaction(target.getUniqueId()) != null) {
            return false;
        }
        if (faction.getBanned().contains(target.getUniqueId())) {
            return false;
        }

        clearInviteFromMap(invites, target.getUniqueId(), faction.getId());
        altInvites.computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        save();
        return true;
    }

    public boolean deleteInvite(Player player, String factionName) {
        Faction faction = getFactionByName(factionName);
        if (faction == null) {
            return false;
        }
        boolean removed = clearInviteFromMap(invites, player.getUniqueId(), faction.getId());
        removed |= clearInviteFromMap(altInvites, player.getUniqueId(), faction.getId());
        if (!removed) {
            return false;
        }
        save();
        return true;
    }

    public List<String> getInviteFactionNames(Player player) {
        List<String> names = new ArrayList<>();
        appendInviteNames(names, invites.get(player.getUniqueId()), false);
        appendInviteNames(names, altInvites.get(player.getUniqueId()), true);
        return names;
    }

    public boolean hasInvite(Player player, Faction faction) {
        return hasInviteInMap(invites, player.getUniqueId(), faction.getId())
                || hasInviteInMap(altInvites, player.getUniqueId(), faction.getId());
    }

    public boolean joinFaction(Player player, String factionName) {
        if (getFaction(player.getUniqueId()) != null) {
            return false;
        }
        Faction faction = getFactionByName(factionName);
        if (faction == null) {
            return false;
        }
        if (faction.getBanned().contains(player.getUniqueId())) {
            return false;
        }

        boolean altInvited = hasInviteInMap(altInvites, player.getUniqueId(), faction.getId());
        boolean invited = hasInviteInMap(invites, player.getUniqueId(), faction.getId());
        if (!faction.isOpen() && !invited && !altInvited) {
            return false;
        }

        FactionRole joinRole = altInvited ? FactionRole.ALT : faction.getDefaultRank();
        FactionPlayer member = new FactionPlayer(player, joinRole);
        faction.addMember(member);
        playerToFaction.put(player.getUniqueId(), faction.getId());
        clearInviteFromMap(invites, player.getUniqueId(), faction.getId());
        clearInviteFromMap(altInvites, player.getUniqueId(), faction.getId());
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
        if (kickerMember == null || !kickerMember.getRole().canKick()) {
            return false;
        }

        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null || target.isLeader()) {
            return false;
        }
        if (!kickerMember.getRole().isAtLeast(target.getRole())) {
            return false;
        }

        faction.removeMember(target.getUniqueId());
        playerToFaction.remove(target.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, target));
        save();
        return true;
    }

    public boolean banPlayer(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return false;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target != null) {
            if (target.isLeader() || !actorMember.getRole().isAtLeast(target.getRole())) {
                return false;
            }
            kickPlayer(actor, targetName);
        }
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null) {
            faction.getBanned().add(online.getUniqueId());
        } else if (target != null) {
            faction.getBanned().add(target.getUniqueId());
        } else {
            return false;
        }
        save();
        return true;
    }

    public boolean unbanPlayer(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return false;
        }
        for (UUID bannedId : new HashSet<>(faction.getBanned())) {
            FactionPlayer member = findMemberByUuidAcrossServer(bannedId, targetName);
            if (member != null || matchesOfflineBanName(bannedId, targetName)) {
                faction.getBanned().remove(bannedId);
                save();
                return true;
            }
        }
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null && faction.getBanned().remove(online.getUniqueId())) {
            save();
            return true;
        }
        return false;
    }

    public boolean setRelation(Player actor, String otherFactionName, String relationType) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null || relationType == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.isLeader()) {
            return false;
        }
        Faction other = getFactionByName(otherFactionName);
        if (other == null || other.getId().equals(faction.getId())) {
            return false;
        }

        clearRelationsBetween(faction, other);
        String type = relationType.toLowerCase();
        if ("ally".equals(type)) {
            faction.getAllyIds().add(other.getId());
            other.getAllyIds().add(faction.getId());
        } else if ("enemy".equals(type)) {
            faction.getEnemyIds().add(other.getId());
            other.getEnemyIds().add(faction.getId());
        } else if ("truce".equals(type)) {
            faction.getTruceIds().add(other.getId());
            other.getTruceIds().add(faction.getId());
        } else if (!"neutral".equals(type)) {
            return false;
        }
        save();
        return true;
    }

    public boolean setOpen(Player player, boolean open) {
        Faction faction = requireLeaderFaction(player);
        if (faction == null) {
            return false;
        }
        faction.setOpen(open);
        save();
        return true;
    }

    public boolean setPeaceful(Player player, boolean peaceful) {
        Faction faction = requireLeaderFaction(player);
        if (faction == null) {
            return false;
        }
        faction.setPeaceful(peaceful);
        save();
        return true;
    }

    public boolean setPermanent(Faction faction, boolean permanent) {
        if (faction == null) {
            return false;
        }
        faction.setPermanent(permanent);
        save();
        return true;
    }

    public boolean setGrace(Faction faction, boolean grace) {
        if (faction == null) {
            return false;
        }
        faction.setGrace(grace);
        save();
        return true;
    }

    public boolean setDescription(Player player, String description) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            return false;
        }
        faction.setDescription(description);
        save();
        return true;
    }

    public boolean setRules(Player player, String rules) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            return false;
        }
        faction.setRules(rules);
        save();
        return true;
    }

    public boolean setTag(Player player, String newName) {
        return renameFaction(player, newName);
    }

    public boolean renameFaction(Player player, String newName) {
        Faction faction = requireLeaderFaction(player);
        if (faction == null) {
            return false;
        }
        if (!isValidFactionName(newName) || doesFactionExist(newName) || isReserved(newName)) {
            return false;
        }
        faction.setName(newName);
        save();
        return true;
    }

    public boolean setDefaultRank(Player player, FactionRole rank) {
        Faction faction = requireLeaderFaction(player);
        if (faction == null || rank == null || rank == FactionRole.LEADER) {
            return false;
        }
        faction.setDefaultRank(rank);
        save();
        return true;
    }

    public boolean setTitle(Player actor, String targetName, String title) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canInvite()) {
            return false;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return false;
        }
        target.setTitle(title);
        save();
        return true;
    }

    public boolean promoteMember(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return false;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null || target.isLeader() || target.getRole().isAlt()) {
            return false;
        }
        if (!actorMember.getRole().isAtLeast(target.getRole())) {
            return false;
        }
        FactionRole promoted = target.getRole().promote();
        if (promoted == target.getRole()) {
            return false;
        }
        if (promoted == FactionRole.LEADER) {
            return transferLeadership(actor, targetName);
        }
        target.setRole(promoted);
        save();
        return true;
    }

    public boolean setRole(Player actor, String targetName, FactionRole role) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null || role == null || role.isAlt()) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return false;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null || target.getRole().isAlt()) {
            return false;
        }
        if (role == FactionRole.LEADER) {
            return transferLeadership(actor, targetName);
        }
        if (target.isLeader() || !actorMember.getRole().isAtLeast(target.getRole())) {
            return false;
        }
        if (role.ordinal() < actorMember.getRole().ordinal()) {
            return false;
        }
        target.setRole(role);
        save();
        return true;
    }

    public boolean transferLeadership(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        FactionPlayer target = faction.getMemberByName(targetName);
        if (actorMember == null || target == null || !actorMember.isLeader() || target.getRole().isAlt()) {
            return false;
        }
        actorMember.setRole(FactionRole.COLEADER);
        target.setRole(FactionRole.LEADER);
        faction.setLeader(target);
        save();
        return true;
    }

    public boolean unclaimAll(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return false;
        }
        return unclaimAll(faction);
    }

    public boolean unclaimAll(Faction faction) {
        if (faction == null) {
            return false;
        }
        for (String claimKey : new HashSet<>(faction.getClaims())) {
            claims.remove(claimKey);
            faction.getClaims().remove(claimKey);
        }
        if (faction.getBaseClaimKey() != null) {
            faction.setBaseClaimKey(null);
        }
        save();
        return true;
    }

    public boolean claimChunk(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return false;
        }
        if (claims.containsKey(claim.toKey())) {
            return false;
        }
        if (faction.getLand() >= faction.getPower()) {
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
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return false;
        }
        UUID ownerId = claims.get(claim.toKey());
        if (ownerId == null || !ownerId.equals(faction.getId())) {
            return false;
        }

        claims.remove(claim.toKey());
        faction.getClaims().remove(claim.toKey());
        if (claim.toKey().equals(faction.getBaseClaimKey())) {
            faction.setBaseClaimKey(null);
        }
        Bukkit.getPluginManager().callEvent(new LandUnclaimEvent(faction, claim));
        save();
        return true;
    }

    public boolean setBaseClaim(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return false;
        }
        UUID ownerId = claims.get(claim.toKey());
        if (ownerId == null || !ownerId.equals(faction.getId())) {
            return false;
        }
        faction.setBaseClaimKey(claim.toKey());
        save();
        return true;
    }

    public boolean setHome(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return false;
        }
        faction.setHome(NamedLocation.from(player.getLocation()));
        save();
        return true;
    }

    public boolean deleteHome(Player player) {
        Faction faction = requireLeaderFaction(player);
        if (faction == null) {
            return false;
        }
        faction.setHome(null);
        save();
        return true;
    }

    public boolean teleportHome(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null || faction.getHome() == null) {
            return false;
        }
        Location location = faction.getHome().toLocation();
        if (location == null) {
            return false;
        }
        return player.teleport(location);
    }

    public boolean setWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null || warpName == null || warpName.isEmpty()) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return false;
        }
        faction.getWarps().put(warpName.toLowerCase(), NamedLocation.from(player.getLocation()));
        save();
        return true;
    }

    public boolean deleteWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null || warpName == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return false;
        }
        if (faction.getWarps().remove(warpName.toLowerCase()) == null) {
            return false;
        }
        save();
        return true;
    }

    public boolean teleportWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null || warpName == null) {
            return false;
        }
        NamedLocation warp = faction.getWarps().get(warpName.toLowerCase());
        if (warp == null) {
            return false;
        }
        Location location = warp.toLocation();
        if (location == null) {
            return false;
        }
        return player.teleport(location);
    }

    public List<String> listWarps(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(faction.getWarps().keySet());
    }

    public boolean depositMoney(Player player, double amount) {
        if (amount <= 0) {
            return false;
        }
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        faction.setMoney(faction.getMoney() + amount);
        save();
        return true;
    }

    public boolean withdrawMoney(Player player, double amount) {
        if (amount <= 0) {
            return false;
        }
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return false;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return false;
        }
        if (faction.getMoney() < amount) {
            return false;
        }
        faction.setMoney(faction.getMoney() - amount);
        save();
        return true;
    }

    public boolean addTnt(Faction faction, int amount) {
        if (faction == null || amount <= 0) {
            return false;
        }
        faction.setTnt(faction.getTnt() + amount);
        save();
        return true;
    }

    public boolean removeTnt(Faction faction, int amount) {
        if (faction == null || amount <= 0 || faction.getTnt() < amount) {
            return false;
        }
        faction.setTnt(faction.getTnt() - amount);
        save();
        return true;
    }

    public boolean setPoints(Faction faction, int points) {
        if (faction == null) {
            return false;
        }
        faction.setPoints(points);
        save();
        return true;
    }

    public boolean setPowerBoost(Faction faction, int boost) {
        if (faction == null) {
            return false;
        }
        faction.setPowerBoost(boost);
        faction.recalculatePower();
        save();
        return true;
    }

    public boolean addStrikes(Faction faction, int strikes) {
        if (faction == null || strikes <= 0) {
            return false;
        }
        faction.setStrikes(faction.getStrikes() + strikes);
        save();
        return true;
    }

    public boolean adminSetStrikes(Faction faction, int strikes) {
        if (faction == null || strikes < 0) {
            return false;
        }
        faction.setStrikes(strikes);
        save();
        return true;
    }

    public boolean setMaxVaults(Faction faction, int maxVaults) {
        if (faction == null) {
            return false;
        }
        faction.setMaxVaults(maxVaults);
        save();
        return true;
    }

    public Location findWildernessTeleport(Player player) {
        World world = player.getWorld();
        ChunkClaim origin = ChunkClaim.from(player.getLocation());
        for (int attempt = 0; attempt < 40; attempt++) {
            int dx = random.nextInt(21) - 10;
            int dz = random.nextInt(21) - 10;
            if (dx == 0 && dz == 0) {
                continue;
            }
            ChunkClaim candidate = new ChunkClaim(world.getName(), origin.getX() + dx, origin.getZ() + dz);
            if (getFactionAt(candidate) == null) {
                return chunkCenter(candidate, world);
            }
        }
        return null;
    }

    public Location findStuckTeleport(Player player) {
        World world = player.getWorld();
        ChunkClaim origin = ChunkClaim.from(player.getLocation());
        for (int radius = 0; radius <= 32; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    ChunkClaim candidate = new ChunkClaim(world.getName(), origin.getX() + dx, origin.getZ() + dz);
                    if (getFactionAt(candidate) == null) {
                        return chunkCenter(candidate, world);
                    }
                }
            }
        }
        return null;
    }

    public Location getCornerLocation(Player player, int index) {
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation();
        int corner = Math.floorMod(index, 4);
        double x = spawn.getX();
        double z = spawn.getZ();
        switch (corner) {
            case 0:
                x += CORNER_OFFSET;
                z += CORNER_OFFSET;
                break;
            case 1:
                x += CORNER_OFFSET;
                z -= CORNER_OFFSET;
                break;
            case 2:
                x -= CORNER_OFFSET;
                z += CORNER_OFFSET;
                break;
            default:
                x -= CORNER_OFFSET;
                z -= CORNER_OFFSET;
                break;
        }
        return new Location(world, x, spawn.getY(), z, spawn.getYaw(), spawn.getPitch());
    }

    public void broadcastToFaction(Faction faction, String path, Object... placeholders) {
        messages.broadcastFaction(faction, path, placeholders);
    }

    public void broadcastToAllies(Faction faction, String path, Object... placeholders) {
        if (faction == null) {
            return;
        }
        messages.broadcastFaction(faction, path, placeholders);
        for (UUID allyId : faction.getAllyIds()) {
            Faction ally = factions.get(allyId);
            if (ally != null) {
                messages.broadcastFaction(ally, path, placeholders);
            }
        }
    }

    public List<Faction> listFactionsSortedByLand() {
        List<Faction> list = new ArrayList<>(factions.values());
        list.sort(Comparator.comparingInt(Faction::getLand).reversed());
        return list;
    }

    public List<Faction> listFactionsSortedByPower() {
        List<Faction> list = new ArrayList<>(factions.values());
        list.sort(Comparator.comparingInt(Faction::getPower).reversed());
        return list;
    }

    public List<Faction> listFactionsSortedByPoints() {
        List<Faction> list = new ArrayList<>(factions.values());
        list.sort(Comparator.comparingInt(Faction::getPoints).reversed());
        return list;
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
        Set<UUID> playerInvites = invites.get(playerId);
        if (playerInvites == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(playerInvites);
    }

    private Faction requireLeaderFaction(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return null;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            return null;
        }
        return faction;
    }

    private void clearRelationsBetween(Faction first, Faction second) {
        first.getAllyIds().remove(second.getId());
        second.getAllyIds().remove(first.getId());
        first.getEnemyIds().remove(second.getId());
        second.getEnemyIds().remove(first.getId());
        first.getTruceIds().remove(second.getId());
        second.getTruceIds().remove(first.getId());
    }

    private void removeRelationsForFaction(UUID factionId) {
        for (Faction other : factions.values()) {
            if (other.getId().equals(factionId)) {
                continue;
            }
            other.getAllyIds().remove(factionId);
            other.getEnemyIds().remove(factionId);
            other.getTruceIds().remove(factionId);
        }
    }

    private void clearAllInvitesForFaction(UUID factionId) {
        clearFactionFromInviteMap(invites, factionId);
        clearFactionFromInviteMap(altInvites, factionId);
    }

    private void clearInvitesToFaction(UUID playerId, UUID factionId) {
        clearInviteFromMap(invites, playerId, factionId);
        clearInviteFromMap(altInvites, playerId, factionId);
    }

    private void appendInviteNames(List<String> names, Set<UUID> factionIds, boolean alt) {
        if (factionIds == null || factionIds.isEmpty()) {
            return;
        }
        for (UUID factionId : factionIds) {
            Faction faction = factions.get(factionId);
            if (faction != null) {
                names.add(alt ? faction.getName() + " (alt)" : faction.getName());
            }
        }
    }

    private static boolean hasInviteInMap(Map<UUID, Set<UUID>> map, UUID playerId, UUID factionId) {
        Set<UUID> playerInvites = map.get(playerId);
        return playerInvites != null && playerInvites.contains(factionId);
    }

    private static boolean clearInviteFromMap(Map<UUID, Set<UUID>> map, UUID playerId, UUID factionId) {
        Set<UUID> playerInvites = map.get(playerId);
        if (playerInvites == null || !playerInvites.remove(factionId)) {
            return false;
        }
        if (playerInvites.isEmpty()) {
            map.remove(playerId);
        }
        return true;
    }

    private static void clearFactionFromInviteMap(Map<UUID, Set<UUID>> map, UUID factionId) {
        for (Set<UUID> factionIds : map.values()) {
            factionIds.remove(factionId);
        }
        map.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private static Location chunkCenter(ChunkClaim claim, World world) {
        return new Location(world, claim.getX() * 16 + 8, world.getHighestBlockYAt(claim.getX() * 16 + 8, claim.getZ() * 16 + 8), claim.getZ() * 16 + 8);
    }

    private FactionPlayer findMemberByUuidAcrossServer(UUID uuid, String name) {
        for (Faction faction : factions.values()) {
            FactionPlayer member = faction.getMember(uuid);
            if (member != null && member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    private boolean matchesOfflineBanName(UUID bannedId, String targetName) {
        for (Faction faction : factions.values()) {
            FactionPlayer member = faction.getMember(bannedId);
            if (member != null && member.getName().equalsIgnoreCase(targetName)) {
                return true;
            }
        }
        return false;
    }
}
