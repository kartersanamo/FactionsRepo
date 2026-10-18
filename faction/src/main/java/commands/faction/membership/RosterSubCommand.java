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
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        msg(player, "roster.header", "faction", faction.getName());
        for (FactionPlayer member : faction.getMembers()) {
            String title = member.getTitle().isEmpty() ? ""
                    : messages.format("roster.title-format", "title", member.getTitle());
            String status = messages.raw(member.isOnline() ? "roster.online" : "roster.offline");
            msg(player, "roster.line", "player", member.getName(), "title", title,
                    "role", member.getRole(), "status", status);
        }
    }
}
