package com.kartersanamo.faction.systems;

import com.kartersanamo.faction.model.FactionChatMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FactionPlayerFlags {
    private final Set<UUID> autoClaim = new HashSet<>();
    private final Set<UUID> bypass = new HashSet<>();
    private final Set<UUID> chatSpy = new HashSet<>();
    private final Set<UUID> fly = new HashSet<>();
    private final Set<UUID> scoreboardEnabled = new HashSet<>();
    private final Set<UUID> seeChunk = new HashSet<>();
    private final Map<UUID, FactionChatMode> chatMode = new HashMap<>();
    private final Map<UUID, Integer> cornerIndex = new HashMap<>();

    public boolean hasAutoClaim(UUID playerId) {
        return autoClaim.contains(playerId);
    }

    public boolean toggleAutoClaim(UUID playerId) {
        return toggleSet(autoClaim, playerId);
    }

    public boolean hasBypass(UUID playerId) {
        return bypass.contains(playerId);
    }

    public boolean toggleBypass(UUID playerId) {
        return toggleSet(bypass, playerId);
    }

    public boolean hasChatSpy(UUID playerId) {
        return chatSpy.contains(playerId);
    }

    public boolean toggleChatSpy(UUID playerId) {
        return toggleSet(chatSpy, playerId);
    }

    public boolean hasFly(UUID playerId) {
        return fly.contains(playerId);
    }

    public boolean toggleFly(UUID playerId) {
        return toggleSet(fly, playerId);
    }

    public void setFly(UUID playerId, boolean enabled) {
        if (enabled) {
            fly.add(playerId);
        } else {
            fly.remove(playerId);
        }
    }

    public boolean hasScoreboardEnabled(UUID playerId) {
        return scoreboardEnabled.contains(playerId);
    }

    public boolean toggleScoreboardEnabled(UUID playerId) {
        return toggleSet(scoreboardEnabled, playerId);
    }

    public boolean hasSeeChunk(UUID playerId) {
        return seeChunk.contains(playerId);
    }

    public boolean toggleSeeChunk(UUID playerId) {
        return toggleSet(seeChunk, playerId);
    }

    public FactionChatMode getChatMode(UUID playerId) {
        return chatMode.getOrDefault(playerId, FactionChatMode.PUBLIC);
    }

    public void setChatMode(UUID playerId, FactionChatMode mode) {
        if (mode == null || mode == FactionChatMode.PUBLIC) {
            chatMode.remove(playerId);
        } else {
            chatMode.put(playerId, mode);
        }
    }

    public Map<UUID, FactionChatMode> getChatModesView() {
        return Collections.unmodifiableMap(chatMode);
    }

    public int nextCornerIndex(UUID playerId) {
        int index = cornerIndex.getOrDefault(playerId, 0);
        cornerIndex.put(playerId, (index + 1) % 4);
        return index;
    }

    private static boolean toggleSet(Set<UUID> set, UUID playerId) {
        if (set.contains(playerId)) {
            set.remove(playerId);
            return false;
        }
        set.add(playerId);
        return true;
    }
}
