package commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class SetMaxVaultsSubCommand extends FactionSubCommand {
    public SetMaxVaultsSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("setmaxvaults")
    @CommandPermission("factions.admin")
    public void onSetMaxVaults(Player player, String factionName, int max) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        factionSystem.setMaxVaults(faction, max);
        msg(player, "vault.max-set", "faction", factionName, "max", max);
    }
}
