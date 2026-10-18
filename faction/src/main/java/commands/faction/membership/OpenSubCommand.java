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
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        boolean next = !faction.isOpen();
        if (!factionSystem.setOpen(player, next)) { msg(player, "open.not-leader"); return; }
        msg(player, next ? "open.now-open" : "open.now-closed");
    }
}
