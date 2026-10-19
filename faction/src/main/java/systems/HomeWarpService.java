package systems;

import model.ChunkClaim;
import model.Faction;
import model.FactionPlayer;
import model.FactionResult;
import model.NamedLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Faction home/warps and teleport destination lookups.
 */
public class HomeWarpService {
    private static final int CORNER_OFFSET = 2000;

    private final FactionRegistry registry;
    private final Random random = new Random();

    public HomeWarpService(FactionRegistry registry) {
        this.registry = registry;
    }

    public FactionResult setHome(Player player) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        ChunkClaim claim = ChunkClaim.from(player.getLocation());
        Faction chunkOwner = registry.getFactionAt(claim);
        if (chunkOwner == null || !chunkOwner.getId().equals(faction.getId())) {
            return FactionResult.NOT_YOUR_CLAIM;
        }

        faction.setHome(NamedLocation.from(player.getLocation()));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteHome(Player player) {
        FactionResult leaderCheck = registry.checkLeader(player);
        if (leaderCheck != FactionResult.SUCCESS) {
            return leaderCheck;
        }
        registry.getFaction(player.getUniqueId()).setHome(null);
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult teleportHome(Player player) {
        // TODO: Add teleport delay and check for movement during the delay
        // TODO: Add combat check (after we combat module)
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (faction.getHome() == null) {
            return FactionResult.NO_HOME;
        }
        Location location = faction.getHome().toLocation();
        if (location == null) {
            return FactionResult.NO_HOME;
        }
        return player.teleport(location) ? FactionResult.SUCCESS : FactionResult.FAILED;
    }

    public FactionResult setWarp(Player player, String warpName) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null || warpName.isEmpty()) {
            return FactionResult.INVALID_NAME;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        faction.getWarps().put(warpName.toLowerCase(), NamedLocation.from(player.getLocation()));
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult deleteWarp(Player player, String warpName) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null) {
            return FactionResult.INVALID_NAME;
        }
        FactionPlayer member = faction.getMember(player.getUniqueId());
        if (member == null || !member.getRole().canSetHome()) {
            return FactionResult.NO_PERMISSION;
        }
        if (faction.getWarps().remove(warpName.toLowerCase()) == null) {
            return FactionResult.WARP_MISSING;
        }
        registry.save();
        return FactionResult.SUCCESS;
    }

    public FactionResult teleportWarp(Player player, String warpName) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return FactionResult.NOT_IN_FACTION;
        }
        if (warpName == null) {
            return FactionResult.INVALID_NAME;
        }
        NamedLocation warp = faction.getWarps().get(warpName.toLowerCase());
        if (warp == null) {
            return FactionResult.WARP_MISSING;
        }
        Location location = warp.toLocation();
        if (location == null) {
            return FactionResult.FAILED;
        }
        return player.teleport(location) ? FactionResult.SUCCESS : FactionResult.FAILED;
    }

    public List<String> listWarps(Player player) {
        Faction faction = registry.getFaction(player.getUniqueId());
        if (faction == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(faction.getWarps().keySet());
    }

    public Location findWildernessTeleport(Player player) {
        World world = player.getWorld();
        ChunkClaim origin = ChunkClaim.from(player.getLocation());
        for (int attempt = 0; attempt < 40; attempt++) {
            int dx = random.nextInt(21) - 10;
            int dz = random.nextInt(21) - 10;
            if (dx == 0 && dz == 0) {
                continue;
            }
            ChunkClaim candidate = new ChunkClaim(world.getName(), origin.getX() + dx, origin.getZ() + dz);
            if (registry.getFactionAt(candidate) == null) {
                return chunkCenter(candidate, world);
            }
        }
        return null;
    }

    public Location findStuckTeleport(Player player) {
        World world = player.getWorld();
        ChunkClaim origin = ChunkClaim.from(player.getLocation());
        for (int radius = 0; radius <= 32; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    ChunkClaim candidate = new ChunkClaim(world.getName(), origin.getX() + dx, origin.getZ() + dz);
                    if (registry.getFactionAt(candidate) == null) {
                        return chunkCenter(candidate, world);
                    }
                }
            }
        }
        return null;
    }

    public Location getCornerLocation(Player player, int index) {
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation();
        int corner = Math.floorMod(index, 4);
        double x = spawn.getX();
        double z = spawn.getZ();
        switch (corner) {
            case 0:
                x += CORNER_OFFSET;
                z += CORNER_OFFSET;
                break;
            case 1:
                x += CORNER_OFFSET;
                z -= CORNER_OFFSET;
                break;
            case 2:
                x -= CORNER_OFFSET;
                z += CORNER_OFFSET;
                break;
            default:
                x -= CORNER_OFFSET;
                z -= CORNER_OFFSET;
                break;
        }
        return new Location(world, x, spawn.getY(), z, spawn.getYaw(), spawn.getPitch());
    }

    private static Location chunkCenter(ChunkClaim claim, World world) {
        int blockX = claim.getX() * 16 + 8;
        int blockZ = claim.getZ() * 16 + 8;
        return new Location(world, blockX, world.getHighestBlockYAt(blockX, blockZ), blockZ);
    }
}
