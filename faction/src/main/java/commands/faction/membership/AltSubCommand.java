package commands.faction.membership;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class AltSubCommand extends FactionSubCommand {

    public AltSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("alt")
    @CommandCompletion("@players")
    public void onAltCommand(Player player, String targetName) {
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
            msg(player, "alt.self");
            return;
        }

        FactionResult result = factionSystem.inviteAlt(player, target);
        switch (result) {
            case SUCCESS:
                msg(player, "alt.success", "player", target.getName(), "faction", faction.getName());
                msg(target, "alt.notified", "faction", faction.getName());
                break;
            case NO_PERMISSION:
                msg(player, "alt.not-leader");
                break;
            case ALREADY_MEMBER:
                msg(player, "alt.already-member");
                break;
            default:
                msg(player, "alt.failed");
                break;
        }
    }
}
