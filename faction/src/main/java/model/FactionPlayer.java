package model;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class FactionPlayer {
    private final Player player;

    public FactionPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FactionPlayer)) {
            return false;
        }
        return getUniqueId().equals(((FactionPlayer) other).getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueId());
    }
}
