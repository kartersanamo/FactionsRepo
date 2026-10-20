package com.kartersanamo.network;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class NetworkPlugin extends Plugin {

    private Configuration config;
    private Configuration messages;
    private RebootManager rebootManager;

    @Override
    public void onEnable() {
        saveDefault("config.yml");
        saveDefault("messages.yml");
        reloadFiles();

        File requestDir = new File(getDataFolder(), config.getString("request-dir", "restart-requests"));
        if (!requestDir.exists() && !requestDir.mkdirs()) {
            getLogger().severe("Could not create restart request directory: " + requestDir);
        }
        File scheduleDir = new File(getDataFolder(), "schedule-requests");
        if (!scheduleDir.exists() && !scheduleDir.mkdirs()) {
            getLogger().severe("Could not create schedule request directory: " + scheduleDir);
        }

        rebootManager = new RebootManager(this);
        getProxy().getPluginManager().registerCommand(this, new RebootCommand(this, rebootManager));
        getProxy().getPluginManager().registerListener(this, rebootManager);
        rebootManager.recoverQueues();
        rebootManager.startSchedulePolling();
        getLogger().info("Network reboot command registered.");
    }

    @Override
    public void onDisable() {
        if (rebootManager != null) {
            rebootManager.shutdown();
        }
    }

    public void reloadFiles() {
        config = loadYaml("config.yml");
        messages = loadYaml("messages.yml");
    }

    public Configuration config() {
        return config;
    }

    public String message(String key, String... replacements) {
        String text = messages.getString(key, key);
        String prefix = messages.getString("prefix", "");
        text = text.replace("%prefix%", prefix);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void saveDefault(String name) {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().warning("Could not create data folder " + getDataFolder());
        }
        File file = new File(getDataFolder(), name);
        if (file.exists()) {
            return;
        }
        try (InputStream in = getResourceAsStream(name)) {
            if (in == null) {
                getLogger().severe("Missing jar resource " + name);
                return;
            }
            Files.copy(in, file.toPath());
        } catch (IOException exception) {
            getLogger().log(Level.SEVERE, "Could not save " + name, exception);
        }
    }

    private Configuration loadYaml(String name) {
        File file = new File(getDataFolder(), name);
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException exception) {
            getLogger().log(Level.SEVERE, "Could not load " + name, exception);
            return new Configuration();
        }
    }
}
