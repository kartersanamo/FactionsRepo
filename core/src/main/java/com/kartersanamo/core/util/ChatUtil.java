package com.kartersanamo.core.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ChatUtil {

    private ChatUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void send(Player player, String message) {
        if (player == null) {
            return;
        }
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }
        player.spigot().sendMessage(components(message));
    }

    private static BaseComponent[] components(String message) {
        List<BaseComponent> parts = new ArrayList<>();
        String remaining = message;
        String consumed = "";
        while (!remaining.isEmpty()) {
            Match match = nextMatch(remaining);
            if (match == null) {
                addLegacy(parts, remaining);
                break;
            }
            if (match.index > 0) {
                String prefix = remaining.substring(0, match.index);
                addLegacy(parts, prefix);
                consumed += prefix;
            }
            String phrase = remaining.substring(match.index, match.index + match.length);
            for (BaseComponent component : TextComponent.fromLegacyText(
                    ChatColor.getLastColors(consumed) + phrase
            )) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, match.url));
                parts.add(component);
            }
            consumed += phrase;
            remaining = remaining.substring(match.index + match.length);
        }
        return parts.toArray(new BaseComponent[0]);
    }

    private static void addLegacy(List<BaseComponent> parts, String legacy) {
        parts.addAll(List.of(TextComponent.fromLegacyText(legacy)));
    }

    private static Match nextMatch(String text) {
        int terms = indexOfIgnoreCase(text, "terms of service");
        int privacy = indexOfIgnoreCase(text, "privacy policy");
        if (terms < 0 && privacy < 0) {
            return null;
        }
        if (privacy < 0 || (terms >= 0 && terms < privacy)) {
            return new Match(terms, "terms of service".length(), ServerBrandingUtil.getTermsOfService());
        }
        return new Match(privacy, "privacy policy".length(), ServerBrandingUtil.getPrivacyPolicy());
    }

    private static int indexOfIgnoreCase(String text, String phrase) {
        return text.toLowerCase().indexOf(phrase);
    }

    private record Match(int index, int length, String url) {
    }
}
