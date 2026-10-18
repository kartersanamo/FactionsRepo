package commands.faction.role;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class BanSubCommand extends FactionSubCommand {
    public BanSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ban")
    @CommandCompletion("@players")
    public void onBan(Player player, String target) {
        if (!factionSystem.banPlayer(player, target)) {
            msg(player, "ban.failed");
            return;
        }
        msg(player, "ban.success", "player", target);
    }
}
