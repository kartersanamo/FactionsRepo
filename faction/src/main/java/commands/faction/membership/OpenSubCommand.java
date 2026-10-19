package commands.faction.membership;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class OpenSubCommand extends FactionSubCommand {
    public OpenSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("open")
    public void onOpen(Player player) {
        Faction faction = factionSystem.getFaction(player.getUniqueId());
        if (faction == null) { msg(player, "general.not-in-faction"); return; }
        boolean next = !faction.isOpen();
        FactionResult result = factionSystem.setOpen(player, next);
        switch (result) {
            case SUCCESS:
                msg(player, next ? "open.now-open" : "open.now-closed");
                break;
            case NOT_LEADER:
                msg(player, "open.not-leader");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
