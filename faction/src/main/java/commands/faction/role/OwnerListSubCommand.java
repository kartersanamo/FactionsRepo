package commands.faction.role;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import model.FactionRole;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class OwnerListSubCommand extends FactionSubCommand {
    public OwnerListSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("ownerlist")
    public void onOwnerList(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        player.sendMessage("Leadership for " + faction.getName() + ":");
        for (FactionPlayer member : faction.getMembers()) {
            if (member.getRole() == FactionRole.LEADER || member.getRole() == FactionRole.COLEADER) {
                player.sendMessage("- " + member.getName() + " (" + member.getRole() + ")");
            }
        }
    }
}
