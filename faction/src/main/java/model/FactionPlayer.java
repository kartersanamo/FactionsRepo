package model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class FactionPlayer {
    private final UUID uuid;
    private String name;
    private FactionRole role;

    public FactionPlayer(Player player, FactionRole role) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.role = role;
    }

    public FactionPlayer(UUID uuid, String name, FactionRole role) {
        this.uuid = uuid;
        this.name = name;
        this.role = role;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FactionRole getRole() {
        return role;
    }

    public void setRole(FactionRole role) {
        this.role = role;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public boolean isLeader() {
        return role == FactionRole.LEADER;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FactionPlayer)) {
            return false;
        }
        return uuid.equals(((FactionPlayer) other).uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
