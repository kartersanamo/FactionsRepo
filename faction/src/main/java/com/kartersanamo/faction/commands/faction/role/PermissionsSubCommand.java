package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

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
