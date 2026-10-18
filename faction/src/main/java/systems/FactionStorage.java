package systems;

import model.Faction;
import model.FactionPlayer;
import model.FactionRole;
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

        if (!dataFile.exists()) {
            return new LoadedData(factions, playerToFaction, claims, invites);
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection factionsSection = yaml.getConfigurationSection("factions");
        if (factionsSection == null) {
            return new LoadedData(factions, playerToFaction, claims, invites);
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
                    FactionRole role = FactionRole.valueOf(memberSection.getString("role", "MEMBER"));
                    FactionPlayer member = new FactionPlayer(memberId, name, role);
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

            Faction faction = new Faction(factionId, leader, section.getString("name"));
            faction.getMembers().clear();
            faction.getMembers().addAll(members);
            faction.setLeader(leader);
            faction.setDescription(section.getString("description", ""));
            faction.setPower(section.getInt("power"));
            faction.setMaxPower(section.getInt("maxPower"));
            if (section.contains("creationDate")) {
                faction.setCreationDate(new Date(section.getLong("creationDate")));
            }

            Set<String> claimKeys = new HashSet<>(section.getStringList("claims"));
            faction.setClaims(claimKeys);
            for (String claimKey : claimKeys) {
                claims.put(claimKey, factionId);
            }

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

        return new LoadedData(factions, playerToFaction, claims, invites);
    }

    public void save(
            Map<UUID, Faction> factions,
            Map<UUID, Set<UUID>> invites
    ) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        ConfigurationSection factionsSection = yaml.createSection("factions");

        for (Faction faction : factions.values()) {
            ConfigurationSection section = factionsSection.createSection(faction.getId().toString());
            section.set("name", faction.getName());
            section.set("description", faction.getDescription());
            section.set("power", faction.getPower());
            section.set("maxPower", faction.getMaxPower());
            section.set("creationDate", faction.getCreationDate().getTime());
            section.set("claims", new ArrayList<>(faction.getClaims()));

            ConfigurationSection membersSection = section.createSection("members");
            for (FactionPlayer member : faction.getMembers()) {
                ConfigurationSection memberSection = membersSection.createSection(member.getUniqueId().toString());
                memberSection.set("name", member.getName());
                memberSection.set("role", member.getRole().name());
            }
        }

        ConfigurationSection invitesSection = yaml.createSection("invites");
        for (Map.Entry<UUID, Set<UUID>> entry : invites.entrySet()) {
            List<String> factionIds = new ArrayList<>();
            for (UUID factionId : entry.getValue()) {
                factionIds.add(factionId.toString());
            }
            invitesSection.set(entry.getKey().toString(), factionIds);
        }

        if (!dataFile.getParentFile().exists() && !dataFile.getParentFile().mkdirs()) {
            throw new IOException("Could not create data folder: " + dataFile.getParentFile());
        }
        yaml.save(dataFile);
    }

    public static final class LoadedData {
        public final Map<UUID, Faction> factions;
        public final Map<UUID, UUID> playerToFaction;
        public final Map<String, UUID> claims;
        public final Map<UUID, Set<UUID>> invites;

        public LoadedData(
                Map<UUID, Faction> factions,
                Map<UUID, UUID> playerToFaction,
                Map<String, UUID> claims,
                Map<UUID, Set<UUID>> invites
        ) {
            this.factions = factions;
            this.playerToFaction = playerToFaction;
            this.claims = claims;
            this.invites = invites;
        }
    }
}
