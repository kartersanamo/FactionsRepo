package com.kartersanamo.faction.systems;

import com.kartersanamo.faction.model.ChunkClaim;
import com.kartersanamo.faction.messages.Messages;
import com.kartersanamo.faction.model.Faction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClaimMapRenderer {
    private static final int WIDTH = 46;
    private static final int HEIGHT = 16;
    private static final char YOU = '+';
    private static final char WILDERNESS = '-';
    private static final char[] FACTION_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789".toCharArray();
    private static final ChatColor[] FACTION_COLORS = {
            ChatColor.DARK_GREEN,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.BLUE,
            ChatColor.DARK_PURPLE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.RED,
            ChatColor.DARK_RED,
            ChatColor.WHITE
    };

    private final FactionSystem factionSystem;
    private final Messages messages;

    public ClaimMapRenderer(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
        this.messages = factionSystem.getMessages();
    }

    public void sendMap(Player player) {
        ChunkClaim center = ChunkClaim.from(player.getLocation());
        Faction viewerFaction = factionSystem.getFaction(player.getUniqueId());
        CompassFacing facing = facingFromYaw(player.getLocation().getYaw());

        Map<UUID, Character> symbols = new LinkedHashMap<>();
        Map<UUID, ChatColor> colors = new LinkedHashMap<>();

        List<String> rows = new ArrayList<>();

        final int UP_DOWN_RADIUS = HEIGHT / 2;
        final int LEFT_RIGHT_RADIUS = WIDTH / 2;

        for (int dz = -UP_DOWN_RADIUS; dz <= UP_DOWN_RADIUS; dz++) {
            int mapRow = dz + UP_DOWN_RADIUS;
            StringBuilder row = new StringBuilder();
            for (int dx = -LEFT_RIGHT_RADIUS; dx <= LEFT_RIGHT_RADIUS; dx++) {
                int mapCol = dx + LEFT_RIGHT_RADIUS;
                // Compass overlay
                if (mapRow < 3 && mapCol == 0) {
                    row.append(compassLine(mapRow, facing));
                    dx += 2;
                    continue;
                }
                int chunkX = center.getX() + dx;
                int chunkZ = center.getZ() + dz;
                ChunkClaim claim = new ChunkClaim(center.getWorldName(), chunkX, chunkZ);
                boolean isYou = dx == 0 && dz == 0;
                row.append(cell(claim, viewerFaction, isYou, symbols, colors));
            }
            rows.add(row.toString());
        }

        Faction centerFaction = factionSystem.getFactionAt(center);
        ChatColor headerColor = centerFaction != null
                ? colors.getOrDefault(centerFaction.getId(), ChatColor.WHITE)
                : ChatColor.GRAY;
        messages.send(player, "map.header",
                "x", String.valueOf(center.getX()),
                "z", String.valueOf(center.getZ()),
                "color", headerColor.toString(),
                "faction", centerFaction != null ? centerFaction.getName() : messages.format("map.wilderness")
        );
        for (String row : rows) {
            messages.sendRaw(player, row);
        }
        sendLegend(player, symbols, colors);
    }

    private enum CompassFacing {
        NORTH,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTH,
        SOUTHWEST,
        WEST,
        NORTHWEST;

        boolean isDiagonal() {
            return this == NORTHEAST || this == SOUTHEAST || this == SOUTHWEST || this == NORTHWEST;
        }
    }

    private static CompassFacing facingFromYaw(float yaw) {
        float normalized = (yaw % 360f + 360f) % 360f;
        // Minecraft: 0 = south, 90 = west, 180 = north, 270 = east
        int octant = Math.round(normalized / 45f) % 8;
        switch (octant) {
            case 1:
                return CompassFacing.SOUTHWEST;
            case 2:
                return CompassFacing.WEST;
            case 3:
                return CompassFacing.NORTHWEST;
            case 4:
                return CompassFacing.NORTH;
            case 5:
                return CompassFacing.NORTHEAST;
            case 6:
                return CompassFacing.EAST;
            case 7:
                return CompassFacing.SOUTHEAST;
            case 0:
            default:
                return CompassFacing.SOUTH;
        }
    }

    private static String compassLine(int mapRow, CompassFacing facing) {
        String off = "&8";
        String on = "&2";
        return switch (mapRow) {
            case 0 -> color(facing == CompassFacing.NORTHWEST, on, off) + "\\"
                    + color(facing == CompassFacing.NORTH, on, off) + "N"
                    + color(facing == CompassFacing.NORTHEAST, on, off) + "/";
            case 1 -> color(facing == CompassFacing.WEST, on, off) + "W"
                    + off + "+"
                    + color(facing == CompassFacing.EAST, on, off) + "E";
            case 2 -> color(facing == CompassFacing.SOUTHWEST, on, off) + "/"
                    + color(facing == CompassFacing.SOUTH, on, off) + "S"
                    + color(facing == CompassFacing.SOUTHEAST, on, off) + "\\";
            default -> "";
        };
    }

    private static String color(boolean active, String on, String off) {
        return active ? on : off;
    }

    private String cell(
            ChunkClaim claim,
            Faction viewerFaction,
            boolean isYou,
            Map<UUID, Character> symbols,
            Map<UUID, ChatColor> colors
    ) {
        Faction owner = factionSystem.getFactionAt(claim);

        // Still register the faction for legend/header color even when this cell is YOU (+)
        if (owner != null) {
            symbolFor(owner, symbols);
            colorFor(owner, viewerFaction, colors);
        }

        if (isYou) {
            return ChatColor.AQUA.toString() + ChatColor.BOLD + YOU + ChatColor.RESET;
        }
        if (owner == null) {
            return ChatColor.GRAY.toString() + WILDERNESS;
        }

        return colors.get(owner.getId()).toString() + symbols.get(owner.getId());
    }

    private char symbolFor(Faction faction, Map<UUID, Character> symbols) {
        Character existing = symbols.get(faction.getId());
        if (existing != null) {
            return existing;
        }
        char next = FACTION_CHARS[symbols.size() % FACTION_CHARS.length];
        symbols.put(faction.getId(), next);
        return next;
    }

    private ChatColor colorFor(Faction faction, Faction viewerFaction, Map<UUID, ChatColor> colors) {
        ChatColor existing = colors.get(faction.getId());
        if (existing != null) {
            return existing;
        }

        ChatColor color;
        if (viewerFaction != null && viewerFaction.getId().equals(faction.getId())) {
            color = ChatColor.GREEN;
        } else {
            color = FACTION_COLORS[colors.size() % FACTION_COLORS.length];
            if (color == ChatColor.GREEN) {
                color = ChatColor.RED;
            }
        }
        colors.put(faction.getId(), color);
        return color;
    }

    private void sendLegend(
            Player player,
            Map<UUID, Character> symbols,
            Map<UUID, ChatColor> colors
    ) {
        if (symbols.isEmpty()) {
            return;
        }

        StringBuilder legend = new StringBuilder();
        boolean first = true;
        for (Map.Entry<UUID, Character> entry : symbols.entrySet()) {
            Faction faction = factionSystem.getFactionById(entry.getKey());
            if (faction == null) {
                continue;
            }
            String color = colors.get(entry.getKey()).toString();
            String entryText = messages.format(
                    "map.legend-entry",
                    "color", color,
                    "symbol", entry.getValue(),
                    "faction", faction.getName()
            );
            legend.append(entryText);
            first = false;
        }

        if (!first) {
            messages.sendRaw(player, legend.toString());
        }
    }
}
