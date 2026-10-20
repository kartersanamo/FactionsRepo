package com.kartersanamo.faction.commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import com.kartersanamo.faction.commands.FactionSubCommand;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.ClaimMapRenderer;
import com.kartersanamo.faction.systems.FactionSystem;

public class MapSubCommand extends FactionSubCommand {

    private final ClaimMapRenderer mapRenderer;

    public MapSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
        this.mapRenderer = new ClaimMapRenderer(factionSystem);
    }

    @Subcommand("map")
    public void onMapCommand(Player player) {
        mapRenderer.sendMap(player);
    }
}
