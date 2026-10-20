package com.kartersanamo.faction.commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionResult;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class StrikesSubCommand extends FactionSubCommand {
    public StrikesSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("strikes")
    @CommandPermission("factions.admin")
    public void onStrikes(Player player, String factionName, @Optional Integer value) {
        Faction faction = factionSystem.getFactionByName(factionName);
        if (faction == null) { msg(player, "general.faction-not-found"); return; }
        if (value == null) {
            msg(player, "admin.strikes-view", "faction", factionName, "strikes", faction.getStrikes());
            return;
        }
        FactionResult result = factionSystem.adminSetStrikes(faction, value);
        switch (result) {
            case SUCCESS:
                msg(player, "admin.strikes-set", "faction", factionName, "strikes", value);
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
