package commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class PermissionsSubCommand extends FactionSubCommand {
    public PermissionsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("perms|permissions")
    public void onPerms(Player player) {
        msg(player, "perms.header");
        msg(player, "perms.leader");
        msg(player, "perms.coleader");
        msg(player, "perms.mod");
        msg(player, "perms.member");
    }
}
