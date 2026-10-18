package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PromoteSubCommand extends FactionSubCommand {
    public PromoteSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("promote")
    @CommandCompletion("@players")
    public void onPromote(Player player, String target) {
        if (!factionSystem.promoteMember(player, target)) {
            msg(player, "promote.failed");
            return;
        }
        msg(player, "promote.success", "player", target);
    }
}
