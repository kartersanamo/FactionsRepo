package commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ScoreboardSubCommand extends FactionSubCommand {
    public ScoreboardSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("scoreboard|sb")
    public void onScoreboard(Player player) {
        boolean enabled = factionSystem.getFlags().toggleScoreboardEnabled(player.getUniqueId());
        player.sendMessage("Faction scoreboard " + (enabled ? "enabled" : "disabled") + " (sidebar coming later).");
    }
}
