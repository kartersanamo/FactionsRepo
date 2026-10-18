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
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        if (rules == null || rules.isEmpty()) {
            String current = faction.getRules();
            if (current.isEmpty()) msg(player, "rules.none");
            else msg(player, "rules.view", "rules", current);
            return;
        }
        if (!factionSystem.setRules(player, rules)) {
            msg(player, "rules.failed");
            return;
        }
        msg(player, "rules.success");
    }
}
