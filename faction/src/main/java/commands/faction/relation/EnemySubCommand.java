package commands.faction.relation;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class EnemySubCommand extends FactionSubCommand {
    public EnemySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("enemy")
    public void onCommand(Player player, String factionName) {
        if (!factionSystem.setRelation(player, factionName, "enemy")) {
            msg(player, "relation.failed");
            return;
        }
        msg(player, "relation.set", "relation", "enemy", "faction", factionName);
    }
}
