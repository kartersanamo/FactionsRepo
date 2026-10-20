package com.kartersanamo.faction.commands.faction.membership;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class InviteSubCommand extends FactionSubCommand {

    public InviteSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void onInviteCommand(Player player, String targetName) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            msg(player, "general.player-not-found");
            return;
        }
        if (target.equals(player)) {
            msg(player, "invite.self");
            return;
        }

        FactionResult result = factionSystem.invitePlayer(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "invite.success", "player", target.getName(), "faction", faction.getName());
                msg(target, "invite.notified", "faction", faction.getName());
                break;
            case NO_PERMISSION:
                msg(player, "invite.not-leader");
                break;
            case ALREADY_MEMBER:
                msg(player, "invite.already-member");
                break;
            default:
                msg(player, "invite.failed");
                break;
        }
    }
}
