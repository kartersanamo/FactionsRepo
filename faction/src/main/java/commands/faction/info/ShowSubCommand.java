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
            player.sendMessage(target == null
                    ? "You are not in a faction."
                    : "Faction not found.");
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
        player.sendMessage("Faction: " + faction.getName());
        player.sendMessage("Leader: " + faction.getLeader().getName());
        player.sendMessage("Members: " + faction.getMembers().size());
        player.sendMessage("Land: " + faction.getLand());
        player.sendMessage("Allies: " + faction.getAllies().size());
        player.sendMessage("Enemies: " + faction.getEnemies().size());
    }
}
