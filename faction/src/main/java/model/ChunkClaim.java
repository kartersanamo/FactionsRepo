package model;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public final class ChunkClaim {
    private final String worldName;
    private final int x;
    private final int z;

    public ChunkClaim(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public static ChunkClaim from(Location location) {
        Chunk chunk = location.getChunk();
        return new ChunkClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkClaim from(Chunk chunk) {
        return new ChunkClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkClaim fromKey(String key) {
        String[] parts = key.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid claim key: " + key);
        }
        return new ChunkClaim(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    public String toKey() {
        return worldName + ":" + x + ":" + z;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isIn(World world) {
        return world != null && world.getName().equals(worldName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChunkClaim)) {
            return false;
        }
        ChunkClaim claim = (ChunkClaim) other;
        return x == claim.x && z == claim.z && worldName.equals(claim.worldName);
    }

    @Override
    public int hashCode() {
        return toKey().hashCode();
    }
}
