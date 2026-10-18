package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class OwnerSubCommand extends FactionSubCommand {
    public OwnerSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("owner|leader")
    @CommandCompletion("@players")
    public void onOwner(Player player, String target) {
        if (!factionSystem.transferLeadership(player, target)) {
            msg(player, "owner.failed");
            return;
        }
        msg(player, "owner.success", "player", target);
    }
}
