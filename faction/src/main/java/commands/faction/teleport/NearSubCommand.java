package commands.faction.teleport;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class NearSubCommand extends FactionSubCommand {
    public NearSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("near")
    public void onNear(Player player) {
        Faction self = factionSystem.getFaction(player.getUniqueId());
        msg(player, "teleport.near-header");
        int found = 0;
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            if (other.getWorld() != player.getWorld()) continue;
            double dist = other.getLocation().distance(player.getLocation());
            if (dist > 200) continue;
            Faction otherFac = factionSystem.getFaction(other.getUniqueId());
            String fac = otherFac == null ? messages.raw("general.wilderness") : otherFac.getName();
            String mark = "";
            if (self != null && otherFac != null) {
                if (self.getId().equals(otherFac.getId())) mark = messages.raw("teleport.near-member");
                else if (self.getEnemyIds().contains(otherFac.getId())) mark = messages.raw("teleport.near-enemy");
                else if (self.getAllyIds().contains(otherFac.getId())) mark = messages.raw("teleport.near-ally");
            }
            msg(player, "teleport.near-line", "player", other.getName(), "faction", fac, "mark", mark, "distance", (int) dist);
            found++;
        }
        if (found == 0) msg(player, "teleport.near-none");
    }
}
