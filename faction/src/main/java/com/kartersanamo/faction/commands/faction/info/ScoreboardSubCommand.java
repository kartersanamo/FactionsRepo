package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

public class ScoreboardSubCommand extends FactionSubCommand {
    public ScoreboardSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("scoreboard|sb")
    public void onScoreboard(Player player) {
        boolean enabled = factionSystem.getFlags().toggleScoreboardEnabled(player.getUniqueId());
        msg(player, enabled ? "scoreboard.on" : "scoreboard.off");
    }
}
