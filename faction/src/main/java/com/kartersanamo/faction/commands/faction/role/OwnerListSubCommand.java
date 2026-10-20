package com.kartersanamo.faction.commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import com.kartersanamo.faction.model.FactionRole;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class OwnerListSubCommand extends FactionSubCommand {
    public OwnerListSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ownerlist")
    public void onOwnerList(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        msg(player, "ownerlist.header", "faction", faction.getName());
        for (FactionPlayer member : faction.getMembers()) {
            if (member.getRole() == FactionRole.LEADER || member.getRole() == FactionRole.COLEADER) {
                msg(player, "ownerlist.line", "player", member.getName(), "role", member.getRole());
            }
        }
    }
}
