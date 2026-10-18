package commands.faction.membership;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ReserveSubCommand extends FactionSubCommand {
    public ReserveSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("reserve")
    @CommandPermission("factions.reserve")
    public void onReserve(Player player, String name) {
        if (!factionSystem.isValidFactionName(name)) { msg(player, "reserve.invalid"); return; }
        if (factionSystem.doesFactionExist(name) || factionSystem.isReserved(name)) {
            msg(player, "reserve.taken");
            return;
        }
        factionSystem.reserveName(name);
        msg(player, "reserve.success", "faction", name);
    }
}
