package commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PermissionsSubCommand extends FactionSubCommand {
    public PermissionsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("perms|permissions")
    public void onPerms(Player player) {
        player.sendMessage("Role permissions:");
        player.sendMessage("LEADER: all");
        player.sendMessage("COLEADER: invite, kick, claim, home");
        player.sendMessage("MOD: invite, claim");
        player.sendMessage("MEMBER: basic");
    }
}
