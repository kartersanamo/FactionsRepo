package com.kartersanamo.home.models;

import com.kartersanamo.home.HomePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class Home {
    private final OfflinePlayer owner;
    private final String id;
    private String displayName;
    private List<String> lore;
    private Material material;
    private Location location;
    private final long createdAt;
    private long lastUsedAt;

    public Home(
            String id,
            OfflinePlayer owner,
            String displayName,
            List<String> lore,
            Material material,
            Location location,
            long createdAt,
            long lastUsedAt
    ) {
        this.owner = owner;
        this.displayName = displayName;
        this.lore = lore;
        this.material = material;
        this.location = location;
        this.id = id;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
    }

    public void teleport() {
        setLastUsedAt(System.currentTimeMillis());
        Player player = owner != null ? owner.getPlayer() : null;
        if (player != null) {
            HomePlugin.getInstance().getTeleportManager().teleportToLocation(player, location);
        }
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> newLore) {
        lore = newLore;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material newMaterial) {
        material = newMaterial;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location newLocation) {
        location = newLocation;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(long timestamp) {
        this.lastUsedAt = timestamp;
    }
}
