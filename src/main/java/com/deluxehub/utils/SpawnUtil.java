package com.deluxehub.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

public class SpawnUtil {
    
    private DeluxeHub plugin;
    private Location spawnLocation;
    
    public SpawnUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        loadSpawnLocation();
    }
    
    private void loadSpawnLocation() {
        ConfigurationSection spawnSection = plugin.getConfig().getConfigurationSection("spawn.location");
        if (spawnSection == null) {
            // Default spawn location
            spawnLocation = new Location(Bukkit.getWorld("world"), 0, 64, 0, 0, 0);
            return;
        }
        
        String worldName = spawnSection.getString("world", "world");
        double x = spawnSection.getDouble("x", 0);
        double y = spawnSection.getDouble("y", 64);
        double z = spawnSection.getDouble("z", 0);
        float yaw = (float) spawnSection.getDouble("yaw", 0);
        float pitch = (float) spawnSection.getDouble("pitch", 0);
        
        if (Bukkit.getWorld(worldName) != null) {
            spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            plugin.getLogger().warning("World '" + worldName + "' not found! Using default spawn.");
            spawnLocation = new Location(Bukkit.getWorlds().get(0), 0, 64, 0, 0, 0);
        }
    }
    
    public void teleportToSpawn(Player player) {
        if (spawnLocation != null) {
            player.teleport(spawnLocation);
            player.sendMessage(plugin.getMessage("teleported-lobby"));
        }
    }
    
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        
        // Save to config
        plugin.getConfig().set("spawn.location.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.location.x", location.getX());
        plugin.getConfig().set("spawn.location.y", location.getY());
        plugin.getConfig().set("spawn.location.z", location.getZ());
        plugin.getConfig().set("spawn.location.yaw", location.getYaw());
        plugin.getConfig().set("spawn.location.pitch", location.getPitch());
        plugin.saveConfig();
        
        plugin.getLogger().info("Spawn location set to: " + location.toString());
    }
    
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    
    public boolean shouldTeleportOnJoin() {
        return plugin.getConfig().getBoolean("spawn.teleport-on-join", true);
    }
    
    public boolean shouldTeleportOnRespawn() {
        return plugin.getConfig().getBoolean("spawn.teleport-on-respawn", true);
    }
}
