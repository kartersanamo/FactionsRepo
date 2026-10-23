package com.kartersanamo.core.util;

import lombok.Getter;
import org.bukkit.ChatColor;

public final class ServerBrandingUtil {

    @Getter
    private static final ChatColor primaryColor = ChatColor.GREEN;
    @Getter
    private static final ChatColor secondaryColor = ChatColor.DARK_GREEN;
    @Getter
    private static final String serverName = "Logix";

    // Prevent instantiation
    private ServerBrandingUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
