package commands.faction.claim;

import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.ClaimMapRenderer;
import systems.FactionSystem;

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
