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
import model.FactionRelation;
import model.FactionResult;
import model.FactionRole;
import model.NamedLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    public FactionResult createFaction(Player player, String factionName) {
        if (getFaction(player.getUniqueId()) != null) {
            return FactionResult.ALREADY_IN_FACTION;
        }
        if (!isValidFactionName(factionName)) {
            return FactionResult.INVALID_NAME;
        }
        if (doesFactionExist(factionName) || isReserved(factionName)) {
            return FactionResult.NAME_TAKEN;
        }

        FactionPlayer owner = new FactionPlayer(player, FactionRole.LEADER);
        UUID factionId = UUID.randomUUID();
        Faction faction = new Faction(factionId, owner, factionName);
        faction.recalculatePower();
        factions.put(factionId, faction);
        playerToFaction.put(player.getUniqueId(), factionId);
        Bukkit.getPluginManager().callEvent(new FactionCreateEvent(faction));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult leaveFaction(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null) {
            return FactionResult.NOT_MEMBER;
        }
        if (member.isLeader()) {
            return FactionResult.IS_LEADER;
        }

        faction.removeMember(player.getUniqueId());
        playerToFaction.remove(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, member));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult disbandFaction(Player player) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        Faction faction = getFaction(player.getUniqueId());
        if (faction.isPermanent()) {
            return FactionResult.PERMANENT;
        }
        return disbandFactionInternal(faction);
    }

    public FactionResult adminDisband(Faction faction) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        return disbandFactionInternal(faction);
    }

    private FactionResult disbandFactionInternal(Faction faction) {
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
        return FactionResult.SUCCESS;
    }

    public FactionResult invitePlayer(Player inviter, Player target) {
        FactionResult inviteCheck = checkCanInvite(inviter, target);
        if (inviteCheck != FactionResult.SUCCESS) {
            return inviteCheck;
        }
        Faction faction = getFaction(inviter.getUniqueId());

        clearInviteFromMap(altInvites, target.getUniqueId(), faction.getId());
        invites.computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult inviteAlt(Player inviter, Player target) {
        FactionResult inviteCheck = checkCanInvite(inviter, target);
        if (inviteCheck != FactionResult.SUCCESS) {
            return inviteCheck;
        }
        Faction faction = getFaction(inviter.getUniqueId());

        clearInviteFromMap(invites, target.getUniqueId(), faction.getId());
        altInvites.computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteInvite(Player player, String factionName) {
        Faction faction = getFactionByName(factionName);
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        boolean removed = clearInviteFromMap(invites, player.getUniqueId(), faction.getId());
        removed |= clearInviteFromMap(altInvites, player.getUniqueId(), faction.getId());
        if (!removed) {
            return FactionResult.INVITE_MISSING;
        }
        save();
        return FactionResult.SUCCESS;
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

    public FactionResult joinFaction(Player player, String factionName) {
        if (getFaction(player.getUniqueId()) != null) {
            return FactionResult.ALREADY_IN_FACTION;
        }
        Faction faction = getFactionByName(factionName);
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (faction.getBanned().contains(player.getUniqueId())) {
            return FactionResult.BANNED;
        }

        boolean altInvited = hasInviteInMap(altInvites, player.getUniqueId(), faction.getId());
        boolean invited = hasInviteInMap(invites, player.getUniqueId(), faction.getId());
        if (!faction.isOpen() && !invited && !altInvited) {
            return FactionResult.NO_INVITE;
        }

        FactionRole joinRole = altInvited ? FactionRole.ALT : faction.getDefaultRank();
        FactionPlayer member = new FactionPlayer(player, joinRole);
        faction.addMember(member);
        playerToFaction.put(player.getUniqueId(), faction.getId());
        clearInviteFromMap(invites, player.getUniqueId(), faction.getId());
        clearInviteFromMap(altInvites, player.getUniqueId(), faction.getId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerJoinEvent(faction, member));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult kickPlayer(Player kicker, String targetName) {
        Faction faction = getFaction(kicker.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer kickerMember = faction.getMember(kicker.getUniqueId());
        if (kickerMember == null || !kickerMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }

        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return FactionResult.NOT_MEMBER;
        }
        if (target.isLeader()) {
            return FactionResult.TARGET_IS_LEADER;
        }
        if (!kickerMember.getRole().isAtLeast(target.getRole())) {
            return FactionResult.NO_PERMISSION;
        }

        faction.removeMember(target.getUniqueId());
        playerToFaction.remove(target.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, target));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult banPlayer(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target != null) {
            FactionResult kickResult = kickPlayer(actor, targetName);
            if (kickResult != FactionResult.SUCCESS) {
                return kickResult;
            }
        }
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null) {
            faction.getBanned().add(online.getUniqueId());
        } else if (target != null) {
            faction.getBanned().add(target.getUniqueId());
        } else {
            return FactionResult.PLAYER_NOT_FOUND;
        }
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unbanPlayer(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }
        for (UUID bannedId : new HashSet<>(faction.getBanned())) {
            FactionPlayer member = findMemberByUuidAcrossServer(bannedId, targetName);
            if (member != null || matchesOfflineBanName(bannedId, targetName)) {
                faction.getBanned().remove(bannedId);
                save();
                return FactionResult.SUCCESS;
            }
        }
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null && faction.getBanned().remove(online.getUniqueId())) {
            save();
            return FactionResult.SUCCESS;
        }
        return FactionResult.PLAYER_NOT_FOUND;
    }

    public FactionResult setRelation(Player actor, String otherFactionName, String relationType) {
        FactionResult leaderCheck = checkLeader(actor);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (relationType == null) {
            return FactionResult.FAILED;
        }
        Faction faction = getFaction(actor.getUniqueId());
        Faction other = getFactionByName(otherFactionName);
        if (other == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (other.getId().equals(faction.getId())) {
            return FactionResult.SELF;
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
            return FactionResult.FAILED;
        }
        save();
        return FactionResult.SUCCESS;
    }

    /**
     * Relation of {@code other} as seen by {@code viewer}.
     * Null factions are treated as neutral (e.g. wilderness).
     */
    public FactionRelation getRelation(Faction viewer, Faction other) {
        if (viewer == null || other == null) {
            return FactionRelation.NEUTRAL;
        }
        if (viewer.getId().equals(other.getId())) {
            return FactionRelation.SELF;
        }
        if (viewer.getAllyIds().contains(other.getId())) {
            return FactionRelation.ALLY;
        }
        if (viewer.getTruceIds().contains(other.getId())) {
            return FactionRelation.TRUCE;
        }
        if (viewer.getEnemyIds().contains(other.getId())) {
            return FactionRelation.ENEMY;
        }
        return FactionRelation.NEUTRAL;
    }

    public ChatColor getRelationColor(Faction viewer, Faction other) {
        return getRelation(viewer, other).getColor();
    }

    public FactionResult setOpen(Player player, boolean open) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        getFaction(player.getUniqueId()).setOpen(open);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPeaceful(Player player, boolean peaceful) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        getFaction(player.getUniqueId()).setPeaceful(peaceful);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPermanent(Faction faction, boolean permanent) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPermanent(permanent);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setGrace(Faction faction, boolean grace) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setGrace(grace);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setDescription(Player player, String description) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        getFaction(player.getUniqueId()).setDescription(description);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setRules(Player player, String rules) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        getFaction(player.getUniqueId()).setRules(rules);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setTag(Player player, String newName) {
        return renameFaction(player, newName);
    }

    public FactionResult renameFaction(Player player, String newName) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (!isValidFactionName(newName)) {
            return FactionResult.INVALID_NAME;
        }
        if (doesFactionExist(newName) || isReserved(newName)) {
            return FactionResult.NAME_TAKEN;
        }
        getFaction(player.getUniqueId()).setName(newName);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setDefaultRank(Player player, FactionRole rank) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (rank == null || rank == FactionRole.LEADER) {
            return FactionResult.INVALID_RANK;
        }
        getFaction(player.getUniqueId()).setDefaultRank(rank);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setTitle(Player actor, String targetName, String title) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canInvite()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return FactionResult.NOT_MEMBER;
        }
        target.setTitle(title);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult promoteMember(Player actor, String targetName) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return FactionResult.NOT_MEMBER;
        }
        if (target.isLeader()) {
            return FactionResult.TARGET_IS_LEADER;
        }
        if (target.getRole().isAlt()) {
            return FactionResult.INVALID_RANK;
        }
        if (!actorMember.getRole().isAtLeast(target.getRole())) {
            return FactionResult.NO_PERMISSION;
        }
        FactionRole promoted = target.getRole().promote();
        if (promoted == target.getRole()) {
            return FactionResult.INVALID_RANK;
        }
        if (promoted == FactionRole.LEADER) {
            return transferLeadership(actor, targetName);
        }
        target.setRole(promoted);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setRole(Player actor, String targetName, FactionRole role) {
        Faction faction = getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (role == null || role.isAlt()) {
            return FactionResult.INVALID_RANK;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return FactionResult.NOT_MEMBER;
        }
        if (target.getRole().isAlt()) {
            return FactionResult.INVALID_RANK;
        }
        if (role == FactionRole.LEADER) {
            return transferLeadership(actor, targetName);
        }
        if (target.isLeader()) {
            return FactionResult.TARGET_IS_LEADER;
        }
        if (!actorMember.getRole().isAtLeast(target.getRole())
                || role.ordinal() < actorMember.getRole().ordinal()) {
            return FactionResult.NO_PERMISSION;
        }
        target.setRole(role);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult transferLeadership(Player actor, String targetName) {
        FactionResult leaderCheck = checkLeader(actor);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        Faction faction = getFaction(actor.getUniqueId());
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        FactionPlayer target = faction.getMemberByName(targetName);
        if (target == null) {
            return FactionResult.NOT_MEMBER;
        }
        if (target.getRole().isAlt()) {
            return FactionResult.INVALID_RANK;
        }
        actorMember.setRole(FactionRole.COLEADER);
        target.setRole(FactionRole.LEADER);
        faction.setLeader(target);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unclaimAll(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        return unclaimAll(faction);
    }

    public FactionResult unclaimAll(Faction faction) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        for (String claimKey : new HashSet<>(faction.getClaims())) {
            claims.remove(claimKey);
            faction.getClaims().remove(claimKey);
        }
        if (faction.getBaseClaimKey() != null) {
            faction.setBaseClaimKey(null);
        }
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult claimChunk(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        if (claims.containsKey(claim.toKey())) {
            return FactionResult.ALREADY_CLAIMED;
        }
        if (faction.getLand() >= faction.getPower()) {
            return FactionResult.NOT_ENOUGH_POWER;
        }

        claims.put(claim.toKey(), faction.getId());
        faction.getClaims().add(claim.toKey());
        Bukkit.getPluginManager().callEvent(new LandClaimEvent(faction, claim));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unclaimChunk(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionResult ownership = checkClaimOwnedBy(faction, claim);
        if (ownership != FactionResult.SUCCESS) {
            return ownership;
        }

        claims.remove(claim.toKey());
        faction.getClaims().remove(claim.toKey());
        if (claim.toKey().equals(faction.getBaseClaimKey())) {
            faction.setBaseClaimKey(null);
        }
        Bukkit.getPluginManager().callEvent(new LandUnclaimEvent(faction, claim));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setBaseClaim(Player player, ChunkClaim claim) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canClaim()) {
            return FactionResult.NO_PERMISSION;
        }
        FactionResult ownership = checkClaimOwnedBy(faction, claim);
        if (ownership != FactionResult.SUCCESS) {
            return ownership;
        }
        faction.setBaseClaimKey(claim.toKey());
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setHome(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction chunkOwner = getFactionAt(claim);
        if (chunkOwner == null || !chunkOwner.getId().equals(faction.getId())) {
            return FactionResult.NOT_YOUR_CLAIM;
        }

        faction.setHome(NamedLocation.from(player.getLocation()));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteHome(Player player) {
        FactionResult leaderCheck = checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        getFaction(player.getUniqueId()).setHome(null);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult teleportHome(Player player) {
        // TODO: Add teleport delay and check for movement during the delay
        // TODO: Add combat check (after we combat module)
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (faction.getHome() == null) {
            return FactionResult.NO_HOME;
        }
        Location location = faction.getHome().toLocation();
        if (location == null) {
            return FactionResult.NO_HOME;
        }
        return player.teleport(location) ? FactionResult.SUCCESS : FactionResult.FAILED;
    }

    public FactionResult setWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null || warpName.isEmpty()) {
            return FactionResult.INVALID_NAME;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        faction.getWarps().put(warpName.toLowerCase(), NamedLocation.from(player.getLocation()));
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null) {
            return FactionResult.INVALID_NAME;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        if (faction.getWarps().remove(warpName.toLowerCase()) == null) {
            return FactionResult.WARP_MISSING;
        }
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult teleportWarp(Player player, String warpName) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null) {
            return FactionResult.INVALID_NAME;
        }
        NamedLocation warp = faction.getWarps().get(warpName.toLowerCase());
        if (warp == null) {
            return FactionResult.WARP_MISSING;
        }
        Location location = warp.toLocation();
        if (location == null) {
            return FactionResult.FAILED;
        }
        return player.teleport(location) ? FactionResult.SUCCESS : FactionResult.FAILED;
    }

    public List<String> listWarps(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(faction.getWarps().keySet());
    }

    public FactionResult depositMoney(Player player, double amount) {
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        faction.setMoney(faction.getMoney() + amount);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult withdrawMoney(Player player, double amount) {
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        if (faction.getMoney() < amount) {
            return FactionResult.NOT_ENOUGH;
        }
        faction.setMoney(faction.getMoney() - amount);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult addTnt(Faction faction, int amount) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setTnt(faction.getTnt() + amount);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult removeTnt(Faction faction, int amount) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (amount <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        if (faction.getTnt() < amount) {
            return FactionResult.NOT_ENOUGH;
        }
        faction.setTnt(faction.getTnt() - amount);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPoints(Faction faction, int points) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPoints(points);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setPowerBoost(Faction faction, int boost) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setPowerBoost(boost);
        faction.recalculatePower();
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult addStrikes(Faction faction, int strikes) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (strikes <= 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setStrikes(faction.getStrikes() + strikes);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult adminSetStrikes(Faction faction, int strikes) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (strikes < 0) {
            return FactionResult.INVALID_AMOUNT;
        }
        faction.setStrikes(strikes);
        save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setMaxVaults(Faction faction, int maxVaults) {
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        faction.setMaxVaults(maxVaults);
        save();
        return FactionResult.SUCCESS;
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

    public void broadcastToTruces(Faction faction, String path, Object... placeholders) {
        if (faction == null) {
            return;
        }
        messages.broadcastFaction(faction, path, placeholders);
        for (UUID truceId : faction.getTruceIds()) {
            Faction truce = factions.get(truceId);
            if (truce != null) {
                messages.broadcastFaction(truce, path, placeholders);
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

    private FactionResult checkLeader(Player player) {
        Faction faction = getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.isLeader()) {
            return FactionResult.NOT_LEADER;
        }
        return FactionResult.SUCCESS;
    }

    private FactionResult checkCanInvite(Player inviter, Player target) {
        Faction faction = getFaction(inviter.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer inviterMember = faction.getMember(inviter.getUniqueId());
        if (inviterMember == null || !inviterMember.getRole().canInvite()) {
            return FactionResult.NO_PERMISSION;
        }
        if (getFaction(target.getUniqueId()) != null) {
            return FactionResult.ALREADY_MEMBER;
        }
        if (faction.getBanned().contains(target.getUniqueId())) {
            return FactionResult.BANNED;
        }
        return FactionResult.SUCCESS;
    }

    private FactionResult checkClaimOwnedBy(Faction faction, ChunkClaim claim) {
        UUID ownerId = claims.get(claim.toKey());
        if (ownerId == null) {
            return FactionResult.NOT_CLAIMED;
        }
        if (!ownerId.equals(faction.getId())) {
            return FactionResult.OTHER_CLAIM;
        }
        return FactionResult.SUCCESS;
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
