package com.kartersanamo.faction.commands.faction.economy;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class GiveBoosterSubCommand extends FactionSubCommand {
    public GiveBoosterSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("givebooster")
    @CommandPermission("factions.admin")
    public void onGiveBooster(Player player, String factionName, String boosterType) {
        if (factionSystem.getFactionByName(factionName) == null) {
            msg(player, "general.faction-not-found");
            return;
        }
        msg(player, "boosters.given", "booster", boosterType, "faction", factionName);
    }
}
