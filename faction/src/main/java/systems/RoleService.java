package systems;

import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import model.FactionRole;
import org.bukkit.entity.Player;

/**
 * Member roles, titles, default rank and leadership transfer.
 */
public class RoleService {
    private final FactionRegistry registry;

    public RoleService(FactionRegistry registry) {
        this.registry = registry;
    }

    public FactionResult setDefaultRank(Player player, FactionRole rank) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        if (rank == null || rank == FactionRole.LEADER) {
            return FactionResult.INVALID_RANK;
        }
        registry.getFaction(player.getUniqueId()).setDefaultRank(rank);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setTitle(Player actor, String targetName, String title) {
        Faction faction = registry.getFaction(actor.getUniqueId());
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
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult promoteMember(Player actor, String targetName) {
        Faction faction = registry.getFaction(actor.getUniqueId());
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
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult setRole(Player actor, String targetName, FactionRole role) {
        Faction faction = registry.getFaction(actor.getUniqueId());
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
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult transferLeadership(Player actor, String targetName) {
        FactionResult leaderCheck = registry.checkLeader(actor);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        Faction faction = registry.getFaction(actor.getUniqueId());
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
        registry.save();
        return FactionResult.SUCCESS;
    }
}
