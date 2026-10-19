package commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionResult;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetMaxVaultsSubCommand extends FactionSubCommand {
    public SetMaxVaultsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setmaxvaults")
    @CommandPermission("factions.admin")
    public void onSetMaxVaults(Player player, String factionName, int max) {
        FactionResult result = factionSystem.setMaxVaults(factionSystem.getFactionByName(factionName), max);
        switch (result) {
            case SUCCESS:
                msg(player, "vault.max-set", "faction", factionName, "max", max);
                break;
            case FACTION_NOT_FOUND:
                msg(player, "general.faction-not-found");
                break;
            default:
                msg(player, "general.could-not");
                break;
        }
    }
}
