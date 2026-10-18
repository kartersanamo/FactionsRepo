package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class OpenSubCommand extends FactionSubCommand {
    public OpenSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("open")
    public void onOpen(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { player.sendMessage("You are not in a faction."); return; }
        boolean next = !faction.isOpen();
        if (!factionSystem.setOpen(player, next)) { player.sendMessage("Only the leader can toggle open."); return; }
        player.sendMessage("Faction is now " + (next ? "open" : "closed") + ".");
    }
}
