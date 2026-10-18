package commands.faction.info;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class RulesSubCommand extends FactionSubCommand {
    public RulesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("rules")
    public void onRules(Player player, @Optional String rules) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        if (rules == null || rules.isEmpty()) {
            String current = faction.getRules();
            player.sendMessage(current.isEmpty() ? "No rules set." : "Rules: " + current);
            return;
        }
        if (!factionSystem.setRules(player, rules)) {
            player.sendMessage("Could not set rules.");
            return;
        }
        player.sendMessage("Rules updated.");
    }
}
