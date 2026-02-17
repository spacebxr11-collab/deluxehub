package com.deluxehub.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.deluxehub.DeluxeHub;

public class ConfigUtil {
    
    private DeluxeHub plugin;
    private FileConfiguration config;
    
    public ConfigUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public boolean isFeatureEnabled(String feature) {
        return config.getBoolean("features." + feature, true);
    }
    
    public String getMessage(String path) {
        return plugin.color(config.getString("messages." + path, "&cMessage not found: " + path));
    }
    
    public ConfigurationSection getJoinItems() {
        return config.getConfigurationSection("join-items");
    }
    
    public ConfigurationSection getServerSelector() {
        return config.getConfigurationSection("server-selector");
    }
    
    public ConfigurationSection getLaunchpads() {
        return config.getConfigurationSection("launchpads");
    }
    
    public double getLaunchpadVelocityMultiplier() {
        return config.getDouble("launchpads.velocity-multiplier", 2.5);
    }
    
    public double getLaunchpadHeightMultiplier() {
        return config.getDouble("launchpads.height-multiplier", 1.5);
    }
    
    public boolean isActionBarEnabled() {
        return config.getBoolean("actionbar.enabled", true);
    }
    
    public String getActionBarMessage() {
        return plugin.color(config.getString("actionbar.message", "&aWelcome to DeluxeHub!"));
    }
    
    public boolean isWorldProtectionEnabled(String protection) {
        return config.getBoolean("world-protection." + protection, true);
    }
    
    public boolean isJoinFeatureEnabled(String feature) {
        return config.getBoolean("join." + feature + ".enabled", false);
    }
    
    public boolean isChatFeatureEnabled(String feature) {
        return config.getBoolean("chat." + feature + ".enabled", false);
    }
    
    public ConfigurationSection getJoinSection() {
        return config.getConfigurationSection("join");
    }
    
    public ConfigurationSection getChatSection() {
        return config.getConfigurationSection("chat");
    }
    
    public ConfigurationSection getSpawnSection() {
        return config.getConfigurationSection("spawn");
    }
    
    public ConfigurationSection getScoreboardSection() {
        return config.getConfigurationSection("scoreboard");
    }
    
    public ConfigurationSection getTablistSection() {
        return config.getConfigurationSection("tablist");
    }
    
    public ConfigurationSection getAnnouncementsSection() {
        return config.getConfigurationSection("announcements");
    }
    
    public ConfigurationSection getDoubleJumpSection() {
        return config.getConfigurationSection("double-jump");
    }
    
    public ConfigurationSection getHologramsSection() {
        return config.getConfigurationSection("holograms");
    }
    
    public ConfigurationSection getAntiWorldDownloaderSection() {
        return config.getConfigurationSection("anti-world-downloader");
    }
    
    public ConfigurationSection getCustomMenusSection() {
        return config.getConfigurationSection("custom-menus");
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
}
