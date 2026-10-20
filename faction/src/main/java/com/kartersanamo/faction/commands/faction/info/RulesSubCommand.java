package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

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
        FactionResult result = factionSystem.setRules(player, rules);
        switch (result) {
            case SUCCESS:
                msg(player, "rules.success");
                break;
            case NOT_IN_FACTION:
                msg(player, "general.not-in-faction");
                break;
            case NOT_LEADER:
                msg(player, "general.not-leader");
                break;
            default:
                msg(player, "rules.failed");
                break;
        }
    }
}
