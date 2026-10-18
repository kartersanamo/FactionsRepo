package systems;

import model.ChunkClaim;
import model.Faction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClaimMapRenderer {
    private static final int RADIUS = 8;
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

    public ClaimMapRenderer(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
    }

    public void sendMap(Player player) {
        ChunkClaim center = ChunkClaim.from(player.getLocation());
        Faction viewerFaction = factionSystem.getFaction(player.getUniqueId());

        Map<UUID, Character> symbols = new LinkedHashMap<>();
        Map<UUID, ChatColor> colors = new LinkedHashMap<>();

        List<String> rows = new ArrayList<>();
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            StringBuilder row = new StringBuilder();
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int chunkX = center.getX() + dx;
                int chunkZ = center.getZ() + dz;
                ChunkClaim claim = new ChunkClaim(center.getWorldName(), chunkX, chunkZ);
                boolean isYou = dx == 0 && dz == 0;
                row.append(cell(claim, viewerFaction, isYou, symbols, colors));
            }
            rows.add(row.toString());
        }

        player.sendMessage(ChatColor.GOLD + "------------ " + ChatColor.WHITE + "N" + ChatColor.GOLD + " ------------");
        for (String row : rows) {
            player.sendMessage(row);
        }
        player.sendMessage(ChatColor.GOLD + "W <--------+--------> E");
        player.sendMessage(ChatColor.GOLD + "------------ " + ChatColor.WHITE + "S" + ChatColor.GOLD + " ------------");
        sendLegend(player, viewerFaction, symbols, colors);
    }

    private String cell(
            ChunkClaim claim,
            Faction viewerFaction,
            boolean isYou,
            Map<UUID, Character> symbols,
            Map<UUID, ChatColor> colors
    ) {
        if (isYou) {
            return ChatColor.WHITE.toString() + ChatColor.BOLD + YOU + ChatColor.RESET;
        }

        Faction owner = factionSystem.getFactionAt(claim);
        if (owner == null) {
            return ChatColor.GRAY.toString() + WILDERNESS;
        }

        char symbol = symbolFor(owner, symbols);
        ChatColor color = colorFor(owner, viewerFaction, colors);
        return color.toString() + symbol;
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
            Faction viewerFaction,
            Map<UUID, Character> symbols,
            Map<UUID, ChatColor> colors
    ) {
        player.sendMessage(ChatColor.GRAY + String.valueOf(WILDERNESS) + ChatColor.WHITE + " Wilderness  "
                + ChatColor.WHITE + ChatColor.BOLD + YOU + ChatColor.RESET + ChatColor.WHITE + " You");

        for (Map.Entry<UUID, Character> entry : symbols.entrySet()) {
            Faction faction = factionSystem.getFactionById(entry.getKey());
            if (faction == null) {
                continue;
            }
            ChatColor color = colors.get(entry.getKey());
            String relation = "";
            if (viewerFaction != null && viewerFaction.getId().equals(faction.getId())) {
                relation = ChatColor.GREEN + " (you)";
            }
            player.sendMessage(color.toString() + entry.getValue() + ChatColor.WHITE + " " + faction.getName() + relation);
        }
    }
}
