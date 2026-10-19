package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class JoinSubCommand extends FactionSubCommand {

    public JoinSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("join")
    public void onJoinCommand(Player player, String factionName) {
        Faction faction = factionSystem.getFactionByName(factionName);
        FactionResult result = factionSystem.joinFaction(player, factionName);
        switch (result) {
            case SUCCESS:
                msg(player, "join.success", "faction", faction != null ? faction.getName() : factionName);
                break;
            case ALREADY_IN_FACTION:
                msg(player, "general.already-in-faction");
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            case BANNED:
                msg(player, "join.banned");
                break;
            case NO_INVITE:
                msg(player, "join.no-invite");
                break;
            default:
                msg(player, "join.failed");
                break;
        }
    }
}
