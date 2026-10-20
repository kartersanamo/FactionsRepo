package com.kartersanamo.home.managers;

import com.kartersanamo.core.config.YamlFile;
import com.kartersanamo.core.util.LocationUtil;
import com.kartersanamo.core.util.TimeUtil;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomeManager {

    private final Map<UUID, Map<String, Home>> homes;
    private final Plugin plugin;

    public HomeManager(Plugin plugin) {
        this.plugin = plugin;
        this.homes = new HashMap<>();
    }

    private YamlFile getPlayerFile(UUID uuid) {
        return new YamlFile(plugin, "homes/" + uuid + ".yml");
    }

    public Set<UUID> getKnownHomeOwners() {
        Set<UUID> ownerIds = new HashSet<>();
        File homesDir = new File(plugin.getDataFolder(), "homes");
        File[] files = homesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) {
            return ownerIds;
        }

        for (File file : files) {
            String name = file.getName().substring(0, file.getName().length() - 4);
            try {
                ownerIds.add(UUID.fromString(name));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ownerIds;
    }

    public Set<String> getKnownHomeOwnerNames() {
        Set<String> ownerNames = new HashSet<>();
        for (UUID ownerId : getKnownHomeOwners()) {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(ownerId);
            if (offlinePlayer.getName() != null && !offlinePlayer.getName().isBlank()) {
                ownerNames.add(offlinePlayer.getName());
            }
        }
        return ownerNames;
    }

    public UUID resolvePlayerUuid(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return null;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return player.getUniqueId();
            }
        }

        for (UUID ownerId : getKnownHomeOwners()) {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(ownerId);
            String name = offlinePlayer.getName();
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return ownerId;
            }
        }
        return null;
    }

    public void loadHomes(UUID uuid) {
        YamlFile file = getPlayerFile(uuid);
        ConfigurationSection section = file.raw().getConfigurationSection("homes");
        if (section == null) {
            homes.put(uuid, new HashMap<>());
            return;
        }

        Map<String, Home> playerHomes = new HashMap<>();
        for (String homeId : section.getKeys(false)) {
            ConfigurationSection homeSection = section.getConfigurationSection(homeId);
            if (homeSection == null) {
                continue;
            }

            Location location = LocationUtil.fromConfig(homeSection, "location");
            if (location == null) {
                continue;
            }

            String displayName = homeSection.getString("display-name", homeId);
            Material material;
            try {
                material = Material.valueOf(homeSection.getString("material", "CHEST").toUpperCase());
            } catch (IllegalArgumentException e) {
                material = Material.CHEST;
            }

            List<String> lore = homeSection.getStringList("lore");
            OfflinePlayer owner = plugin.getServer().getOfflinePlayer(uuid);
            Home home = new Home(
                    homeId,
                    owner,
                    displayName,
                    lore,
                    material,
                    location,
                    homeSection.getLong("created-at"),
                    homeSection.getLong("last-used-at")
            );
            playerHomes.put(homeId, home);
        }
        homes.put(uuid, playerHomes);
    }

    public void unloadHomes(UUID uuid) {
        homes.remove(uuid);
    }

    private void saveHomes(UUID uuid) {
        YamlFile file = getPlayerFile(uuid);
        file.raw().set("homes", null);

        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) {
            return;
        }

        for (Home home : playerHomes.values()) {
            String path = "homes." + home.getId();
            file.raw().set(path + ".display-name", home.getDisplayName());
            file.raw().set(path + ".material", home.getMaterial().name());
            file.raw().set(path + ".lore", home.getLore());
            file.raw().set(path + ".location", home.getLocation());
            file.raw().set(path + ".created-at", home.getCreatedAt());
            file.raw().set(path + ".last-used-at", home.getLastUsedAt());
        }
        file.save();
    }

    public void addHome(UUID uuid, Home home) {
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(home.getId(), home);
        saveHomes(uuid);
    }

    public boolean exists(UUID uuid, String name) {
        return getHome(uuid, name) != null;
    }

    public Map<String, Home> getHomes(UUID uuid) {
        ensureLoaded(uuid);
        return homes.get(uuid);
    }

    public Home getHome(UUID uuid, String name) {
        ensureLoaded(uuid);
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null || name == null) {
            return null;
        }
        for (Home home : playerHomes.values()) {
            if (home.getDisplayName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public boolean isHomeNameCorrectSize(String newName) {
        int min = HomePlugin.config().getInt("home.name.min-length", 3);
        int max = HomePlugin.config().getInt("home.name.max-length", 16);
        return newName.length() < min || newName.length() > max;
    }

    public void deleteHome(UUID uniqueId, String homeName) {
        ensureLoaded(uniqueId);
        Map<String, Home> playerHomes = homes.get(uniqueId);
        if (playerHomes == null) {
            return;
        }
        Home home = getHome(uniqueId, homeName);
        if (home == null) {
            return;
        }
        playerHomes.remove(home.getId());
        saveHomes(uniqueId);
    }

    public int getHomeCount(UUID uuid) {
        ensureLoaded(uuid);
        Map<String, Home> playerHomes = homes.get(uuid);
        return playerHomes != null ? playerHomes.size() : 0;
    }

    public void update(Home home) {
        UUID uuid = home.getOwner().getUniqueId();
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(home.getId(), home);
        saveHomes(uuid);
    }

    public boolean homeNameHasColor(String homeName) {
        return !ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', homeName)).equals(homeName);
    }

    public void sendInfo(Home home, Player player) {
        player.sendMessage(HomePlugin.messages().get("home-info-divider"));
        player.sendMessage(HomePlugin.messages().get("home-info-title"));
        player.sendMessage(HomePlugin.messages().get("home-info-spacer"));
        for (String line : getInformationLines(home)) {
            player.sendMessage(line);
        }
        player.sendMessage(HomePlugin.messages().get("home-info-divider"));
    }

    public List<String> getInformationLines(Home home) {
        List<String> lines = new ArrayList<>();
        lines.add(HomePlugin.messages().get("home-info-name", "%name%", home.getDisplayName()));
        lines.add(HomePlugin.messages().get("home-info-created", "%time%", TimeUtil.formatUnix(home.getCreatedAt())));
        lines.add(HomePlugin.messages().get("home-info-last-used", "%time%", TimeUtil.formatUnix(home.getLastUsedAt())));
        lines.add(HomePlugin.messages().get("home-info-material", "%material%", home.getMaterial().name()));
        lines.add(HomePlugin.messages().get("home-info-location", "%location%", LocationUtil.format(home.getLocation())));
        if (home.getLore() == null || home.getLore().isEmpty()) {
            lines.add(HomePlugin.messages().get("home-info-lore-none"));
        } else {
            lines.add(HomePlugin.messages().get("home-info-lore-label"));
            for (String loreLine : home.getLore()) {
                lines.add(HomePlugin.messages().get("home-info-lore-entry", "%line%", loreLine));
            }
        }
        return lines;
    }

    private void ensureLoaded(UUID uuid) {
        if (!homes.containsKey(uuid)) {
            loadHomes(uuid);
        }
    }
}
