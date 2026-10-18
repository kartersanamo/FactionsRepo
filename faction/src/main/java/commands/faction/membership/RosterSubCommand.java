package commands.faction.membership;

import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class RosterSubCommand extends FactionSubCommand {
    public RosterSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("roster")
    public void onRoster(Player player, @Optional String factionName) {
        Faction faction = factionName == null || factionName.isEmpty()
                ? factionSystem.getFaction(player.getUniqueId())
                : factionSystem.getFactionByName(factionName);
        if (faction == null) { player.sendMessage("Faction not found."); return; }
        player.sendMessage("Roster for " + faction.getName() + ":");
        for (FactionPlayer member : faction.getMembers()) {
            String title = member.getTitle().isEmpty() ? "" : " [" + member.getTitle() + "]";
            String online = member.isOnline() ? "online" : "offline";
            player.sendMessage("- " + member.getName() + title + " (" + member.getRole() + ", " + online + ")");
        }
    }
}
