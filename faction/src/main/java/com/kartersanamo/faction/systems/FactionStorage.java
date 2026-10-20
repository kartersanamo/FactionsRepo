package com.kartersanamo.faction.systems;

import com.kartersanamo.faction.model.Faction;
import com.kartersanamo.faction.model.FactionPlayer;
import com.kartersanamo.faction.model.FactionRole;
import com.kartersanamo.faction.model.NamedLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class FactionStorage {
    private final File dataFile;
    private final Logger logger;

    public FactionStorage(File dataFolder, Logger logger) {
        this.dataFile = new File(dataFolder, "factions.yml");
        this.logger = logger;
    }

    public LoadedData load() {
        Map<UUID, Faction> factions = new HashMap<>();
        Map<UUID, UUID> playerToFaction = new HashMap<>();
        Map<String, UUID> claims = new HashMap<>();
        Map<UUID, Set<UUID>> invites = new HashMap<>();
        Map<UUID, Set<UUID>> altInvites = new HashMap<>();

        if (!dataFile.exists()) {
            return new LoadedData(factions, playerToFaction, claims, invites, altInvites);
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection factionsSection = yaml.getConfigurationSection("factions");
        if (factionsSection == null) {
            return new LoadedData(factions, playerToFaction, claims, invites, altInvites);
        }

        for (String factionIdText : factionsSection.getKeys(false)) {
            UUID factionId = UUID.fromString(factionIdText);
            ConfigurationSection section = factionsSection.getConfigurationSection(factionIdText);
            if (section == null) {
                continue;
            }

            ConfigurationSection membersSection = section.getConfigurationSection("members");
            List<FactionPlayer> members = new ArrayList<>();
            FactionPlayer leader = null;
            if (membersSection != null) {
                for (String memberIdText : membersSection.getKeys(false)) {
                    UUID memberId = UUID.fromString(memberIdText);
                    ConfigurationSection memberSection = membersSection.getConfigurationSection(memberIdText);
                    if (memberSection == null) {
                        continue;
                    }
                    String name = memberSection.getString("name", "Unknown");
                    FactionRole role = parseRole(memberSection.getString("role", "MEMBER"));
                    FactionPlayer member = new FactionPlayer(memberId, name, role);
                    member.setTitle(memberSection.getString("title", ""));
                    members.add(member);
                    playerToFaction.put(memberId, factionId);
                    if (role == FactionRole.LEADER) {
                        leader = member;
                    }
                }
            }

            if (leader == null && !members.isEmpty()) {
                leader = members.get(0);
                leader.setRole(FactionRole.LEADER);
            }
            if (leader == null) {
                logger.warning("Skipping faction " + factionIdText + ": no members");
                continue;
            }

            Faction faction = new Faction(factionId, leader, section.getString("name", "Unknown"));
            faction.getMembers().clear();
            faction.getMembers().addAll(members);
            faction.setLeader(leader);

            faction.setDescription(section.getString("description", ""));
            faction.setRules(section.getString("rules", ""));
            faction.setPower(section.getInt("power", faction.getPower()));
            faction.setMaxPower(section.getInt("maxPower", faction.getMaxPower()));
            faction.setPowerBoost(section.getInt("powerBoost", 0));
            faction.setPoints(section.getInt("points", 0));
            faction.setTnt(section.getInt("tnt", 0));
            faction.setStrikes(section.getInt("strikes", 0));
            faction.setMaxVaults(section.getInt("maxVaults", faction.getMaxVaults()));
            faction.setMoney(section.getDouble("money", 0));
            faction.setOpen(section.getBoolean("open", false));
            faction.setPeaceful(section.getBoolean("peaceful", false));
            faction.setPermanent(section.getBoolean("permanent", false));
            faction.setGrace(section.getBoolean("grace", false));
            faction.setDefaultRank(parseRole(section.getString("defaultRank", "MEMBER")));

            if (section.contains("creationDate")) {
                faction.setCreationDate(new Date(section.getLong("creationDate")));
            }

            faction.setAllyIds(readUuidSet(section.getStringList("allyIds")));
            faction.setEnemyIds(readUuidSet(section.getStringList("enemyIds")));
            faction.setTruceIds(readUuidSet(section.getStringList("truceIds")));
            faction.setBanned(readUuidSet(section.getStringList("banned")));

            faction.setBaseClaimKey(section.getString("baseClaimKey"));

            String homeRaw = section.getString("home");
            if (homeRaw != null && !homeRaw.isEmpty()) {
                faction.setHome(NamedLocation.fromString(homeRaw));
            }

            ConfigurationSection warpsSection = section.getConfigurationSection("warps");
            Map<String, NamedLocation> warps = new HashMap<>();
            if (warpsSection != null) {
                for (String warpName : warpsSection.getKeys(false)) {
                    String warpRaw = warpsSection.getString(warpName);
                    if (warpRaw != null && !warpRaw.isEmpty()) {
                        warps.put(warpName.toLowerCase(), NamedLocation.fromString(warpRaw));
                    }
                }
            }
            faction.setWarps(warps);

            Set<String> claimKeys = new HashSet<>(section.getStringList("claims"));
            faction.setClaims(claimKeys);
            for (String claimKey : claimKeys) {
                claims.put(claimKey, factionId);
            }

            faction.recalculatePower();
            factions.put(factionId, faction);
        }

        ConfigurationSection invitesSection = yaml.getConfigurationSection("invites");
        if (invitesSection != null) {
            for (String playerIdText : invitesSection.getKeys(false)) {
                UUID playerId = UUID.fromString(playerIdText);
                Set<UUID> factionIds = new HashSet<>();
                for (String factionIdText : invitesSection.getStringList(playerIdText)) {
                    factionIds.add(UUID.fromString(factionIdText));
                }
                invites.put(playerId, factionIds);
            }
        }

        ConfigurationSection altInvitesSection = yaml.getConfigurationSection("altInvites");
        if (altInvitesSection != null) {
            for (String playerIdText : altInvitesSection.getKeys(false)) {
                UUID playerId = UUID.fromString(playerIdText);
                Set<UUID> factionIds = new HashSet<>();
                for (String factionIdText : altInvitesSection.getStringList(playerIdText)) {
                    factionIds.add(UUID.fromString(factionIdText));
                }
                altInvites.put(playerId, factionIds);
            }
        }

        return new LoadedData(factions, playerToFaction, claims, invites, altInvites);
    }

    public void save(
            Map<UUID, Faction> factions,
            Map<UUID, Set<UUID>> invites,
            Map<UUID, Set<UUID>> altInvites
    ) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        ConfigurationSection factionsSection = yaml.createSection("factions");

        for (Faction faction : factions.values()) {
            ConfigurationSection section = factionsSection.createSection(faction.getId().toString());
            section.set("name", faction.getName());
            section.set("description", faction.getDescription());
            section.set("rules", faction.getRules());
            section.set("power", faction.getPower());
            section.set("maxPower", faction.getMaxPower());
            section.set("powerBoost", faction.getPowerBoost());
            section.set("points", faction.getPoints());
            section.set("tnt", faction.getTnt());
            section.set("strikes", faction.getStrikes());
            section.set("maxVaults", faction.getMaxVaults());
            section.set("money", faction.getMoney());
            section.set("open", faction.isOpen());
            section.set("peaceful", faction.isPeaceful());
            section.set("permanent", faction.isPermanent());
            section.set("grace", faction.isGrace());
            section.set("defaultRank", faction.getDefaultRank().name());
            section.set("creationDate", faction.getCreationDate().getTime());
            section.set("claims", new ArrayList<>(faction.getClaims()));
            section.set("baseClaimKey", faction.getBaseClaimKey());
            section.set("allyIds", writeUuidList(faction.getAllyIds()));
            section.set("enemyIds", writeUuidList(faction.getEnemyIds()));
            section.set("truceIds", writeUuidList(faction.getTruceIds()));
            section.set("banned", writeUuidList(faction.getBanned()));

            if (faction.getHome() != null) {
                section.set("home", faction.getHome().serialize());
            }

            ConfigurationSection warpsSection = section.createSection("warps");
            for (Map.Entry<String, NamedLocation> entry : faction.getWarps().entrySet()) {
                warpsSection.set(entry.getKey(), entry.getValue().serialize());
            }

            ConfigurationSection membersSection = section.createSection("members");
            for (FactionPlayer member : faction.getMembers()) {
                ConfigurationSection memberSection = membersSection.createSection(member.getUniqueId().toString());
                memberSection.set("name", member.getName());
                memberSection.set("role", member.getRole().name());
                memberSection.set("title", member.getTitle());
            }
        }

        ConfigurationSection invitesSection = yaml.createSection("invites");
        for (Map.Entry<UUID, Set<UUID>> entry : invites.entrySet()) {
            invitesSection.set(entry.getKey().toString(), writeUuidList(entry.getValue()));
        }

        ConfigurationSection altInvitesSection = yaml.createSection("altInvites");
        for (Map.Entry<UUID, Set<UUID>> entry : altInvites.entrySet()) {
            altInvitesSection.set(entry.getKey().toString(), writeUuidList(entry.getValue()));
        }

        if (!dataFile.getParentFile().exists() && !dataFile.getParentFile().mkdirs()) {
            throw new IOException("Could not create data folder: " + dataFile.getParentFile());
        }
        yaml.save(dataFile);
    }

    private static FactionRole parseRole(String raw) {
        try {
            return FactionRole.valueOf(raw);
        } catch (IllegalArgumentException exception) {
            return FactionRole.MEMBER;
        }
    }

    private static Set<UUID> readUuidSet(List<String> raw) {
        Set<UUID> result = new HashSet<>();
        if (raw == null) {
            return result;
        }
        for (String text : raw) {
            try {
                result.add(UUID.fromString(text));
            } catch (IllegalArgumentException ignored) {
                // skip invalid
            }
        }
        return result;
    }

    private static List<String> writeUuidList(Set<UUID> ids) {
        List<String> result = new ArrayList<>();
        if (ids == null) {
            return result;
        }
        for (UUID id : ids) {
            result.add(id.toString());
        }
        return result;
    }

    public static final class LoadedData {
        public final Map<UUID, Faction> factions;
        public final Map<UUID, UUID> playerToFaction;
        public final Map<String, UUID> claims;
        public final Map<UUID, Set<UUID>> invites;
        public final Map<UUID, Set<UUID>> altInvites;

        public LoadedData(
                Map<UUID, Faction> factions,
                Map<UUID, UUID> playerToFaction,
                Map<String, UUID> claims,
                Map<UUID, Set<UUID>> invites,
                Map<UUID, Set<UUID>> altInvites
        ) {
            this.factions = factions;
            this.playerToFaction = playerToFaction;
            this.claims = claims;
            this.invites = invites;
            this.altInvites = altInvites;
        }
    }
}
