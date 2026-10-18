package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionPlayer;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class LoginsSubCommand extends FactionSubCommand {
    public LoginsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("logins")
    public void onLogins(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        player.sendMessage("Online members:");
        for (FactionPlayer member : faction.getMembers()) {
            if (member.isOnline()) player.sendMessage("- " + member.getName());
        }
    }
}
