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
        if (!factionSystem.isValidFactionName(name)) { player.sendMessage("Invalid name."); return; }
        if (factionSystem.doesFactionExist(name) || factionSystem.isReserved(name)) {
            player.sendMessage("That name is already taken or reserved.");
            return;
        }
        factionSystem.reserveName(name);
        player.sendMessage("Reserved faction name " + name + ".");
    }
}
