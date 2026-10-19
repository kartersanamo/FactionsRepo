package systems;

import event.faction.FactionCreateEvent;
import event.faction.FactionDisbandEvent;
import event.player.FactionPlayerJoinEvent;
import event.player.FactionPlayerLeaveEvent;
import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import model.FactionRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

/**
 * Faction lifecycle, membership, invites, bans and basic faction settings.
 */
public class MembershipService {
    private final FactionRegistry registry;
    private final RelationService relations;

    public MembershipService(FactionRegistry registry, RelationService relations) {
        this.registry = registry;
        this.relations = relations;
    }

    public FactionResult createFaction(Player player, String factionName) {
        if (registry.getFaction(player.getUniqueId()) != null) {
            return FactionResult.ALREADY_IN_FACTION;
        }
        if (!registry.isValidFactionName(factionName)) {
            return FactionResult.INVALID_NAME;
        }
        if (registry.doesFactionExist(factionName) || registry.isReserved(factionName)) {
            return FactionResult.NAME_TAKEN;
        }

        FactionPlayer owner = new FactionPlayer(player, FactionRole.LEADER);
        UUID factionId = UUID.randomUUID();
        Faction faction = new Faction(factionId, owner, factionName);
        faction.recalculatePower();
        registry.factions().put(factionId, faction);
        registry.playerToFaction().put(player.getUniqueId(), factionId);
        Bukkit.getPluginManager().callEvent(new FactionCreateEvent(faction));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult leaveFaction(Player player) {
        Faction faction = registry.getFaction(player.getUniqueId());
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
        registry.playerToFaction().remove(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, member));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult disbandFaction(Player player) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        Faction faction = registry.getFaction(player.getUniqueId());
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

    public FactionResult invitePlayer(Player inviter, Player target) {
        FactionResult inviteCheck = registry.checkCanInvite(inviter, target);
        if (inviteCheck != FactionResult.SUCCESS) {
            return inviteCheck;
        }
        Faction faction = registry.getFaction(inviter.getUniqueId());

        FactionRegistry.clearInviteFromMap(registry.altInvites(), target.getUniqueId(), faction.getId());
        registry.invites().computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult inviteAlt(Player inviter, Player target) {
        FactionResult inviteCheck = registry.checkCanInvite(inviter, target);
        if (inviteCheck != FactionResult.SUCCESS) {
            return inviteCheck;
        }
        Faction faction = registry.getFaction(inviter.getUniqueId());

        FactionRegistry.clearInviteFromMap(registry.invites(), target.getUniqueId(), faction.getId());
        registry.altInvites().computeIfAbsent(target.getUniqueId(), ignored -> new HashSet<>()).add(faction.getId());
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteInvite(Player player, String factionName) {
        Faction faction = registry.getFactionByName(factionName);
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        boolean removed = FactionRegistry.clearInviteFromMap(registry.invites(), player.getUniqueId(), faction.getId());
        removed |= FactionRegistry.clearInviteFromMap(registry.altInvites(), player.getUniqueId(), faction.getId());
        if (!removed) {
            return FactionResult.INVITE_MISSING;
        }
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult joinFaction(Player player, String factionName) {
        if (registry.getFaction(player.getUniqueId()) != null) {
            return FactionResult.ALREADY_IN_FACTION;
        }
        Faction faction = registry.getFactionByName(factionName);
        if (faction == null) {
            return FactionResult.FACTION_NOT_FOUND;
        }
        if (faction.getBanned().contains(player.getUniqueId())) {
            return FactionResult.BANNED;
        }

        boolean altInvited = FactionRegistry.hasInviteInMap(registry.altInvites(), player.getUniqueId(), faction.getId());
        boolean invited = FactionRegistry.hasInviteInMap(registry.invites(), player.getUniqueId(), faction.getId());
        if (!faction.isOpen() && !invited && !altInvited) {
            return FactionResult.NO_INVITE;
        }

        FactionRole joinRole = altInvited ? FactionRole.ALT : faction.getDefaultRank();
        FactionPlayer member = new FactionPlayer(player, joinRole);
        faction.addMember(member);
        registry.playerToFaction().put(player.getUniqueId(), faction.getId());
        FactionRegistry.clearInviteFromMap(registry.invites(), player.getUniqueId(), faction.getId());
        FactionRegistry.clearInviteFromMap(registry.altInvites(), player.getUniqueId(), faction.getId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerJoinEvent(faction, member));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult kickPlayer(Player kicker, String targetName) {
        Faction faction = registry.getFaction(kicker.getUniqueId());
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
        registry.playerToFaction().remove(target.getUniqueId());
        Bukkit.getPluginManager().callEvent(new FactionPlayerLeaveEvent(faction, target));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult banPlayer(Player actor, String targetName) {
        Faction faction = registry.getFaction(actor.getUniqueId());
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
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult unbanPlayer(Player actor, String targetName) {
        Faction faction = registry.getFaction(actor.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer actorMember = faction.getMember(actor.getUniqueId());
        if (actorMember == null || !actorMember.getRole().canKick()) {
            return FactionResult.NO_PERMISSION;
        }
        for (UUID bannedId : new HashSet<>(faction.getBanned())) {
            FactionPlayer member = registry.findMemberByUuidAcrossServer(bannedId, targetName);
            if (member != null || registry.matchesOfflineBanName(bannedId, targetName)) {
                faction.getBanned().remove(bannedId);
                registry.save();
                return FactionResult.SUCCESS;
            }
        }
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null && faction.getBanned().remove(online.getUniqueId())) {
            registry.save();
            return FactionResult.SUCCESS;
        }
        return FactionResult.PLAYER_NOT_FOUND;
    }

    public FactionResult setOpen(Player player, boolean open) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        registry.getFaction(player.getUniqueId()).setOpen(open);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setDescription(Player player, String description) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        registry.getFaction(player.getUniqueId()).setDescription(description);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setRules(Player player, String rules) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        registry.getFaction(player.getUniqueId()).setRules(rules);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setTag(Player player, String newName) {
        return renameFaction(player, newName);
    }

    public FactionResult renameFaction(Player player, String newName) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (!registry.isValidFactionName(newName)) {
            return FactionResult.INVALID_NAME;
        }
        if (registry.doesFactionExist(newName) || registry.isReserved(newName)) {
            return FactionResult.NAME_TAKEN;
        }
        registry.getFaction(player.getUniqueId()).setName(newName);
        registry.save();
        return FactionResult.SUCCESS;
    }

    private FactionResult disbandFactionInternal(Faction faction) {
        for (String claimKey : new HashSet<>(faction.getClaims())) {
            registry.claims().remove(claimKey);
        }
        for (FactionPlayer factionMember : new HashSet<>(faction.getMembers())) {
            registry.playerToFaction().remove(factionMember.getUniqueId());
            clearInvitesToFaction(factionMember.getUniqueId(), faction.getId());
        }
        FactionRegistry.clearFactionFromInviteMap(registry.invites(), faction.getId());
        FactionRegistry.clearFactionFromInviteMap(registry.altInvites(), faction.getId());
        relations.removeRelationsForFaction(faction.getId());
        registry.factions().remove(faction.getId());
        Bukkit.getPluginManager().callEvent(new FactionDisbandEvent(faction));
        registry.save();
        return FactionResult.SUCCESS;
    }

    private void clearInvitesToFaction(UUID playerId, UUID factionId) {
        FactionRegistry.clearInviteFromMap(registry.invites(), playerId, factionId);
        FactionRegistry.clearInviteFromMap(registry.altInvites(), playerId, factionId);
    }
}
