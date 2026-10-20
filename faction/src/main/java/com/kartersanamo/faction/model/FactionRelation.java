package com.kartersanamo.faction.model;

import org.bukkit.ChatColor;

public enum FactionRelation {
    SELF(ChatColor.GREEN),
    ALLY(ChatColor.LIGHT_PURPLE),
    TRUCE(ChatColor.AQUA),
    ENEMY(ChatColor.RED),
    NEUTRAL(ChatColor.WHITE);

    private final ChatColor color;

    FactionRelation(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
