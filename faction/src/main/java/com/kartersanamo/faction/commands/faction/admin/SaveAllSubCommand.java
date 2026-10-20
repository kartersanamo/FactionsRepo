package com.kartersanamo.faction.commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class SaveAllSubCommand extends FactionSubCommand {
    public SaveAllSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("saveall|save")
    @CommandPermission("factions.admin")
    public void onSaveAll(Player player) {
        factionSystem.save();
        msg(player, "admin.saved");
    }
}
