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
        player.sendMessage("Nearby players:");
        int found = 0;
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            if (other.getWorld() != player.getWorld()) continue;
            double dist = other.getLocation().distance(player.getLocation());
            if (dist > 200) continue;
            Faction otherFac = factionSystem.getFaction(other.getUniqueId());
            String fac = otherFac == null ? "Wilderness" : otherFac.getName();
            String mark = "";
            if (self != null && otherFac != null) {
                if (self.getId().equals(otherFac.getId())) mark = " (member)";
                else if (self.getEnemyIds().contains(otherFac.getId())) mark = " (enemy)";
                else if (self.getAllyIds().contains(otherFac.getId())) mark = " (ally)";
            }
            player.sendMessage("- " + other.getName() + " [" + fac + "]" + mark + " " + (int) dist + "m");
            found++;
        }
        if (found == 0) player.sendMessage("No players nearby.");
    }
}
