package systems;

import model.ChunkClaim;
import messages.Messages;
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
    private final Messages messages;

    public ClaimMapRenderer(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
        this.messages = factionSystem.getMessages();
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

        messages.send(player, "map.header-north");
        for (String row : rows) {
            messages.sendRaw(player, row);
        }
        messages.send(player, "map.footer-east-west");
        messages.send(player, "map.footer-south");
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
        messages.send(player, "map.legend-wilderness");
        messages.send(player, "map.legend-you");

        for (Map.Entry<UUID, Character> entry : symbols.entrySet()) {
            Faction faction = factionSystem.getFactionById(entry.getKey());
            if (faction == null) {
                continue;
            }
            ChatColor color = colors.get(entry.getKey());
            String you = "";
            if (viewerFaction != null && viewerFaction.getId().equals(faction.getId())) {
                you = messages.raw("map.legend-you-suffix");
            }
            messages.send(player, "map.legend-faction", "color", "&" + color.getChar(),
                    "symbol", entry.getValue(), "faction", faction.getName(), "you", you);
        }
    }
}
