package com.kartersanamo.faction.commands.faction.info;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.kartersanamo.faction.commands.FactionSubCommand;
import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import org.bukkit.entity.Player;
import com.kartersanamo.faction.systems.FactionSystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowSubCommand extends FactionSubCommand {

    private static final SimpleDateFormat FOUNDED_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    public ShowSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @Subcommand("show|who")
    @CommandCompletion("@players")
    @Syntax("[faction|player]")
    public void onShowCommand(Player player, @Optional String target) {
        Faction faction = resolveShowTarget(player, target);
        if (faction == null) {
            msg(player, target == null ? "general.not-in-faction" : "general.faction-not-found");
            return;
        }
        showFaction(player, faction);
    }

    private Faction resolveShowTarget(Player player, String target) {
        if (target == null || target.isEmpty()) {
            return factionSystem.getFaction(player.getUniqueId());
        }
        return factionSystem.findFactionByNameOrPlayer(target);
    }

    private void showFaction(Player player, Faction faction) {
        List<FactionPlayer> online = faction.getOnlineMembers();
        List<FactionPlayer> offline = faction.getOfflineMembers();
        int total = faction.getMainMemberCount();

        String description = faction.getDescription();
        if (description == null || description.isEmpty()) {
            description = "No description set.";
        }

        msg(player, "show",
                "faction", faction.getName(),
                "description", description,
                "owner", faction.getLeader() != null ? faction.getLeader().getName() : "Unknown",
                "land", faction.getLand(),
                "power", faction.getPower(),
                "maxpower", faction.getMaxPower(),
                "shield_status", faction.isShielded() ? "&aOn" : "&cOff",
                "shield_time", faction.isShielded() ? "Grace period" : "No time set.",
                "founded", faction.getCreationDate() != null
                        ? FOUNDED_FORMAT.format(faction.getCreationDate())
                        : "Unknown",
                "allies", coloredList("&d", factionSystem.formatFactionNames(faction.getAllyIds())),
                "truces", coloredList("&e", factionSystem.formatFactionNames(faction.getTruceIds())),
                "enemies", coloredList("&c", factionSystem.formatFactionNames(faction.getEnemyIds())),
                "points", faction.getPoints(),
                "value", faction.getValue(),
                "online", online.size(),
                "offline", offline.size(),
                "total", total,
                "online_list", formatMemberList(online),
                "offline_list", formatMemberList(offline),
                "alts", formatAltList(faction.getAlts())
        );
    }

    private static String coloredList(String color, String names) {
        if ("None".equals(names)) {
            return "&7None";
        }
        return color + names;
    }

    private static String formatMemberList(List<FactionPlayer> members) {
        if (members.isEmpty()) {
            return "&7None";
        }
        List<String> parts = new ArrayList<>();
        for (FactionPlayer member : members) {
            parts.add("&f" + member.getRole().prefix() + member.getName());
        }
        return String.join("&7, ", parts);
    }

    private static String formatAltList(List<FactionPlayer> alts) {
        if (alts.isEmpty()) {
            return "&7None";
        }
        List<String> parts = new ArrayList<>();
        for (FactionPlayer alt : alts) {
            String color = alt.isOnline() ? "&a" : "&7";
            parts.add(color + alt.getName());
        }
        return String.join("&7, ", parts);
    }
}
