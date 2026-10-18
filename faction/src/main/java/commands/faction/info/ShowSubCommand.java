package commands.faction.info;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ShowSubCommand extends FactionSubCommand {

    public ShowSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("show|who")
    @CommandCompletion("@players")
    @Syntax("[faction|player]")
    public void onShowCommand(Player player, @Optional String target) {
        Faction faction = resolveShowTarget(player, target);
        if (faction == null) {
            msg(player, target == null ? "general.not-in-faction" : "general.faction-not-found");
            return;
        }
        showFaction(player, faction);
    }

    private Faction resolveShowTarget(Player player, String target) {
        if (target == null || target.isEmpty()) {
            return factionSystem.getFaction(player.getUniqueId());
        }
        return factionSystem.findFactionByNameOrPlayer(target);
    }

    private void showFaction(Player player, Faction faction) {
        String description = faction.getDescription();
        msg(player, "show",
                "faction", faction.getName(),
                "description", description == null || description.isEmpty() ? "None" : description,
                "leader", faction.getLeader().getName(),
                "members", faction.getMembers().size(),
                "land", faction.getLand(),
                "power", faction.getPower(),
                "maxpower", faction.getMaxPower(),
                "allies", faction.getAllyIds().size(),
                "enemies", faction.getEnemyIds().size(),
                "open", faction.isOpen(),
                "peaceful", faction.isPeaceful());
    }
}
