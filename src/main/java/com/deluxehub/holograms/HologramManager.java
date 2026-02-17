package com.deluxehub.holograms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.deluxehub.DeluxeHub;

public class HologramManager implements Listener {

    private DeluxeHub plugin;
    private List<Hologram> holograms;

    public HologramManager(DeluxeHub plugin) {
        this.plugin = plugin;
        this.holograms = new ArrayList<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin); // Register listener
        loadHolograms();
        startUpdateTask();
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updateAllHolograms();
        }, 20L, 20L); // Update every second
    }

    private void loadHolograms() {
        if (!plugin.getConfigUtil().isFeatureEnabled("holograms")) {
            return;
        }

        ConfigurationSection hologramSection = plugin.getConfig().getConfigurationSection("holograms");
        if (hologramSection == null) {
            return;
        }

        for (String key : hologramSection.getKeys(false)) {
            ConfigurationSection holoSection = hologramSection.getConfigurationSection(key);
            if (holoSection == null)
                continue;

            // Load location
            ConfigurationSection locSection = holoSection.getConfigurationSection("location");
            if (locSection == null)
                continue;

            String worldName = locSection.getString("world");
            double x = locSection.getDouble("x");
            double y = locSection.getDouble("y");
            double z = locSection.getDouble("z");

            if (plugin.getServer().getWorld(worldName) == null) {
                plugin.getLogger().warning("World '" + worldName + "' not found for hologram '" + key + "'");
                continue;
            }

            Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z);

            // Load lines
            List<String> lines = holoSection.getStringList("lines");
            if (lines.isEmpty())
                continue;

            Hologram hologram = new Hologram(plugin, location, lines);
            holograms.add(hologram);

            plugin.getLogger().info("Loaded hologram '" + key + "' with " + lines.size() + " lines");
        }
    }

    public void reloadHolograms() {
        destroyAllHolograms();
        loadHolograms();
        // Packets will be resent by update task
    }

    public void destroyAllHolograms() {
        for (Hologram hologram : holograms) {
            hologram.destroy();
        }
        holograms.clear();
    }

    public void updateAllHolograms() {
        for (Hologram hologram : holograms) {
            hologram.update();
        }
    }

    public List<Hologram> getHolograms() {
        return new ArrayList<>(holograms);
    }

    public Hologram createHologram(Location location, List<String> lines) {
        Hologram hologram = new Hologram(plugin, location, lines);
        holograms.add(hologram);
        return hologram;
    }

    public void removeHologram(Hologram hologram) {
        hologram.destroy();
        holograms.remove(hologram);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Holograms will be spawned by the update task if close enough
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Handled by update task
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        // Handled by update task
    }
}
