package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ConfigSubCommand extends FactionSubCommand {
    public ConfigSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("config")
    @CommandPermission("factions.admin")
    public void onConfig(Player player) {
        player.sendMessage("Config file editing is not wired yet. Use factions.yml data + plugin reload.");
    }
}
