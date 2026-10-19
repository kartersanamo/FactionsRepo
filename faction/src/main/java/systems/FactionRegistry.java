package systems;

import messages.Messages;
import model.ChunkClaim;
import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import model.FactionRole;
import org.bukkit.Bukkit;
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
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Shared faction state, lookups and persistence used by all faction services.
 */
public class FactionRegistry {
    private final FactionStorage storage;
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Messages messages;
    private final FactionPlayerFlags flags = new FactionPlayerFlags();
    private final Set<String> reservedNames = new HashSet<>();

    private Map<UUID, Faction> factions = new HashMap<>();
    private Map<UUID, UUID> playerToFaction = new HashMap<>();
    private Map<String, UUID> claims = new HashMap<>();
    private Map<UUID, Set<UUID>> invites = new HashMap<>();
    private Map<UUID, Set<UUID>> altInvites = new HashMap<>();

    public FactionRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.storage = new FactionStorage(plugin.getDataFolder(), plugin.getLogger());
        this.logger = plugin.getLogger();
        this.messages = new Messages(plugin);
    }

    Map<UUID, Faction> factions() {
        return factions;
    }

    Map<UUID, UUID> playerToFaction() {
        return playerToFaction;
    }

    Map<String, UUID> claims() {
        return claims;
    }

    Map<UUID, Set<UUID>> invites() {
        return invites;
    }

    Map<UUID, Set<UUID>> altInvites() {
        return altInvites;
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

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Messages getMessages() {
        return messages;
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

    public Set<UUID> getInvites(UUID playerId) {
        Set<UUID> playerInvites = invites.get(playerId);
        if (playerInvites == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(playerInvites);
    }

    public boolean hasInvite(Player player, Faction faction) {
        return hasInviteInMap(invites, player.getUniqueId(), faction.getId())
                || hasInviteInMap(altInvites, player.getUniqueId(), faction.getId());
    }

    public List<String> getInviteFactionNames(Player player) {
        List<String> names = new ArrayList<>();
        appendInviteNames(names, invites.get(player.getUniqueId()), false);
        appendInviteNames(names, altInvites.get(player.getUniqueId()), true);
        return names;
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

    FactionResult checkLeader(Player player) {
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

    FactionResult checkCanInvite(Player inviter, Player target) {
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

    FactionPlayer findMemberByUuidAcrossServer(UUID uuid, String name) {
        for (Faction faction : factions.values()) {
            FactionPlayer member = faction.getMember(uuid);
            if (member != null && member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    boolean matchesOfflineBanName(UUID bannedId, String targetName) {
        return findMemberByUuidAcrossServer(bannedId, targetName) != null;
    }

    static boolean hasInviteInMap(Map<UUID, Set<UUID>> map, UUID playerId, UUID factionId) {
        Set<UUID> playerInvites = map.get(playerId);
        return playerInvites != null && playerInvites.contains(factionId);
    }

    static boolean clearInviteFromMap(Map<UUID, Set<UUID>> map, UUID playerId, UUID factionId) {
        Set<UUID> playerInvites = map.get(playerId);
        if (playerInvites == null || !playerInvites.remove(factionId)) {
            return false;
        }
        if (playerInvites.isEmpty()) {
            map.remove(playerId);
        }
        return true;
    }

    static void clearFactionFromInviteMap(Map<UUID, Set<UUID>> map, UUID factionId) {
        for (Set<UUID> factionIds : map.values()) {
            factionIds.remove(factionId);
        }
        map.entrySet().removeIf(entry -> entry.getValue().isEmpty());
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
}
