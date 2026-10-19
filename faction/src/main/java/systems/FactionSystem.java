package systems;

import messages.Messages;
import model.ChunkClaim;
import model.Faction;
import model.FactionPlayer;
import model.FactionRelation;
import model.FactionResult;
import model.FactionRole;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Facade over the faction subsystems; all calls delegate to the registry or a service.
 */
public class FactionSystem {
    private final FactionRegistry registry;
    private final RelationService relations;
    private final MembershipService membership;
    private final RoleService roles;
    private final ClaimService claims;
    private final HomeWarpService homeWarps;
    private final EconomyService economy;

    public FactionSystem(JavaPlugin plugin) {
        this.registry = new FactionRegistry(plugin);
        this.relations = new RelationService(registry);
        this.membership = new MembershipService(registry, relations);
        this.roles = new RoleService(registry);
        this.claims = new ClaimService(registry);
        this.homeWarps = new HomeWarpService(registry);
        this.economy = new EconomyService(registry);
    }

    public FactionRegistry getRegistry() {
        return registry;
    }

    public MembershipService getMembership() {
        return membership;
    }

    public RoleService getRoles() {
        return roles;
    }

    public RelationService getRelations() {
        return relations;
    }

    public ClaimService getClaims() {
        return claims;
    }

    public HomeWarpService getHomeWarps() {
        return homeWarps;
    }

    public EconomyService getEconomy() {
        return economy;
    }

    // Registry

    public void load() {
        registry.load();
    }

    public void save() {
        registry.save();
    }

    public JavaPlugin getPlugin() {
        return registry.getPlugin();
    }

    public Messages getMessages() {
        return registry.getMessages();
    }

    public FactionPlayerFlags getFlags() {
        return registry.getFlags();
    }

    public boolean hasBypass(UUID playerId) {
        return registry.hasBypass(playerId);
    }

    public Collection<Faction> getAllFactions() {
        return registry.getAllFactions();
    }

    public FactionPlayer getFactionPlayer(Player player) {
        return registry.getFactionPlayer(player);
    }

    public Faction getFaction(UUID playerId) {
        return registry.getFaction(playerId);
    }

    public Faction getFaction(FactionPlayer fplayer) {
        return registry.getFaction(fplayer);
    }

    public Faction getFactionById(UUID factionId) {
        return registry.getFactionById(factionId);
    }

    public Faction getFactionByName(String factionName) {
        return registry.getFactionByName(factionName);
    }

    public Faction getFactionAt(ChunkClaim claim) {
        return registry.getFactionAt(claim);
    }

    public Faction findFactionByNameOrPlayer(String input) {
        return registry.findFactionByNameOrPlayer(input);
    }

    public boolean doesFactionExist(String factionName) {
        return registry.doesFactionExist(factionName);
    }

    public boolean isValidFactionName(String factionName) {
        return registry.isValidFactionName(factionName);
    }

    public boolean isReserved(String name) {
        return registry.isReserved(name);
    }

    public void reserveName(String name) {
        registry.reserveName(name);
    }

    public void unreserveName(String name) {
        registry.unreserveName(name);
    }

    public String formatFactionNames(Set<UUID> factionIds) {
        return registry.formatFactionNames(factionIds);
    }

    public List<Faction> listFactionsSortedByLand() {
        return registry.listFactionsSortedByLand();
    }

    public List<Faction> listFactionsSortedByPower() {
        return registry.listFactionsSortedByPower();
    }

    public List<Faction> listFactionsSortedByPoints() {
        return registry.listFactionsSortedByPoints();
    }

    public Set<UUID> getInvites(UUID playerId) {
        return registry.getInvites(playerId);
    }

    public boolean hasInvite(Player player, Faction faction) {
        return registry.hasInvite(player, faction);
    }

    public List<String> getInviteFactionNames(Player player) {
        return registry.getInviteFactionNames(player);
    }

    public void broadcastToFaction(Faction faction, String path, Object... placeholders) {
        registry.broadcastToFaction(faction, path, placeholders);
    }

    public void broadcastToAllies(Faction faction, String path, Object... placeholders) {
        registry.broadcastToAllies(faction, path, placeholders);
    }

    public void broadcastToTruces(Faction faction, String path, Object... placeholders) {
        registry.broadcastToTruces(faction, path, placeholders);
    }

    // Membership

    public FactionResult createFaction(Player player, String factionName) {
        return membership.createFaction(player, factionName);
    }

    public FactionResult leaveFaction(Player player) {
        return membership.leaveFaction(player);
    }

    public FactionResult disbandFaction(Player player) {
        return membership.disbandFaction(player);
    }

    public FactionResult adminDisband(Faction faction) {
        return membership.adminDisband(faction);
    }

    public FactionResult invitePlayer(Player inviter, Player target) {
        return membership.invitePlayer(inviter, target);
    }

    public FactionResult inviteAlt(Player inviter, Player target) {
        return membership.inviteAlt(inviter, target);
    }

    public FactionResult deleteInvite(Player player, String factionName) {
        return membership.deleteInvite(player, factionName);
    }

    public FactionResult joinFaction(Player player, String factionName) {
        return membership.joinFaction(player, factionName);
    }

    public FactionResult kickPlayer(Player kicker, String targetName) {
        return membership.kickPlayer(kicker, targetName);
    }

    public FactionResult banPlayer(Player actor, String targetName) {
        return membership.banPlayer(actor, targetName);
    }

    public FactionResult unbanPlayer(Player actor, String targetName) {
        return membership.unbanPlayer(actor, targetName);
    }

    public FactionResult setOpen(Player player, boolean open) {
        return membership.setOpen(player, open);
    }

    public FactionResult setDescription(Player player, String description) {
        return membership.setDescription(player, description);
    }

    public FactionResult setRules(Player player, String rules) {
        return membership.setRules(player, rules);
    }

    public FactionResult setTag(Player player, String newName) {
        return membership.setTag(player, newName);
    }

    public FactionResult renameFaction(Player player, String newName) {
        return membership.renameFaction(player, newName);
    }

    // Roles

    public FactionResult setDefaultRank(Player player, FactionRole rank) {
        return roles.setDefaultRank(player, rank);
    }

    public FactionResult setTitle(Player actor, String targetName, String title) {
        return roles.setTitle(actor, targetName, title);
    }

    public FactionResult promoteMember(Player actor, String targetName) {
        return roles.promoteMember(actor, targetName);
    }

    public FactionResult setRole(Player actor, String targetName, FactionRole role) {
        return roles.setRole(actor, targetName, role);
    }

    public FactionResult transferLeadership(Player actor, String targetName) {
        return roles.transferLeadership(actor, targetName);
    }

    // Relations

    public FactionResult setRelation(Player actor, String otherFactionName, String relationType) {
        return relations.setRelation(actor, otherFactionName, relationType);
    }

    public FactionRelation getRelation(Faction viewer, Faction other) {
        return relations.getRelation(viewer, other);
    }

    public ChatColor getRelationColor(Faction viewer, Faction other) {
        return relations.getRelationColor(viewer, other);
    }

    public FactionResult setPeaceful(Player player, boolean peaceful) {
        return relations.setPeaceful(player, peaceful);
    }

    public FactionResult setPermanent(Faction faction, boolean permanent) {
        return relations.setPermanent(faction, permanent);
    }

    public FactionResult setGrace(Faction faction, boolean grace) {
        return relations.setGrace(faction, grace);
    }

    // Claims

    public FactionResult claimChunk(Player player, ChunkClaim claim) {
        return claims.claimChunk(player, claim);
    }

    public FactionResult unclaimChunk(Player player, ChunkClaim claim) {
        return claims.unclaimChunk(player, claim);
    }

    public FactionResult unclaimAll(Player player) {
        return claims.unclaimAll(player);
    }

    public FactionResult unclaimAll(Faction faction) {
        return claims.unclaimAll(faction);
    }

    public FactionResult setBaseClaim(Player player, ChunkClaim claim) {
        return claims.setBaseClaim(player, claim);
    }

    public boolean canBuild(Player player, ChunkClaim claim) {
        return claims.canBuild(player, claim);
    }

    // Home and warps

    public FactionResult setHome(Player player) {
        return homeWarps.setHome(player);
    }

    public FactionResult deleteHome(Player player) {
        return homeWarps.deleteHome(player);
    }

    public FactionResult teleportHome(Player player) {
        return homeWarps.teleportHome(player);
    }

    public FactionResult setWarp(Player player, String warpName) {
        return homeWarps.setWarp(player, warpName);
    }

    public FactionResult deleteWarp(Player player, String warpName) {
        return homeWarps.deleteWarp(player, warpName);
    }

    public FactionResult teleportWarp(Player player, String warpName) {
        return homeWarps.teleportWarp(player, warpName);
    }

    public List<String> listWarps(Player player) {
        return homeWarps.listWarps(player);
    }

    public Location findWildernessTeleport(Player player) {
        return homeWarps.findWildernessTeleport(player);
    }

    public Location findStuckTeleport(Player player) {
        return homeWarps.findStuckTeleport(player);
    }

    public Location getCornerLocation(Player player, int index) {
        return homeWarps.getCornerLocation(player, index);
    }

    // Economy

    public FactionResult depositMoney(Player player, double amount) {
        return economy.depositMoney(player, amount);
    }

    public FactionResult withdrawMoney(Player player, double amount) {
        return economy.withdrawMoney(player, amount);
    }

    public FactionResult addTnt(Faction faction, int amount) {
        return economy.addTnt(faction, amount);
    }

    public FactionResult removeTnt(Faction faction, int amount) {
        return economy.removeTnt(faction, amount);
    }

    public FactionResult setPoints(Faction faction, int points) {
        return economy.setPoints(faction, points);
    }

    public FactionResult setPowerBoost(Faction faction, int boost) {
        return economy.setPowerBoost(faction, boost);
    }

    public FactionResult addStrikes(Faction faction, int strikes) {
        return economy.addStrikes(faction, strikes);
    }

    public FactionResult adminSetStrikes(Faction faction, int strikes) {
        return economy.adminSetStrikes(faction, strikes);
    }

    public FactionResult setMaxVaults(Faction faction, int maxVaults) {
        return economy.setMaxVaults(faction, maxVaults);
    }
}
