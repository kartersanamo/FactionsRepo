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
    @Getter
    private static final String serverVersion = "1.0.0";
    @Getter
    private static final String serverAuthor = "Karter Sanamo";
    @Getter
    private static final String serverWebsite = "https://kartersanamo.com";
    @Getter
    private static final String serverDiscord = "https://discord.gg/kartersanamo";
    @Getter
    private static final String termsOfService = "https://kartersanamo.com/terms";
    @Getter
    private static final String privacyPolicy = "https://kartersanamo.com/privacy";

    public static String[] placeholders() {
        return new String[]{
                "%p_color%", "&" + primaryColor.getChar(),
                "%s_color%", "&" + secondaryColor.getChar(),
                "%server%", serverName,
                "%version%", serverVersion,
                "%author%", serverAuthor,
                "%website%", serverWebsite,
                "%discord%", serverDiscord,
                "%terms%", termsOfService,
                "%privacy%", privacyPolicy
        };
    }

    // Prevent instantiation
    private ServerBrandingUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
