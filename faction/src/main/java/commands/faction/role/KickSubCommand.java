package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class KickSubCommand extends FactionSubCommand {

    public KickSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("kick")
    @CommandCompletion("@players")
    public void onKickCommand(Player player, String targetName) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) {
            msg(player, "general.not-in-faction");
            return;
        }

        if (player.getName().equalsIgnoreCase(targetName)) {
            msg(player, "kick.self");
            return;
        }

        FactionPlayer target = faction.getMemberByName(targetName);
        FactionResult result = factionSystem.kickPlayer(player, targetName);
        switch (result) {
            case SUCCESS:
                String kickedName = target != null ? target.getName() : targetName;
                msg(player, "kick.success", "player", kickedName);
                if (target != null) {
                    Player onlineTarget = target.getPlayer();
                    if (onlineTarget != null) {
                        msg(onlineTarget, "kick.notified", "faction", faction.getName());
                    }
                }
                break;
            case NO_PERMISSION:
                msg(player, "kick.not-leader");
                break;
            case NOT_MEMBER:
                msg(player, "kick.not-member");
                break;
            case TARGET_IS_LEADER:
                msg(player, "kick.is-leader");
                break;
            default:
                msg(player, "kick.failed");
                break;
        }
    }
}
