package commands.faction.relation;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class AllySubCommand extends FactionSubCommand {
    public AllySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ally")
    public void onCommand(Player player, String factionName) {
        if (!factionSystem.setRelation(player, factionName, "ally")) {
            msg(player, "relation.failed");
            return;
        }
        msg(player, "relation.set", "relation", "ally", "faction", factionName);
    }
}
