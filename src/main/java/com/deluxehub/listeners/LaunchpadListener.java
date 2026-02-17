package com.deluxehub.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.deluxehub.DeluxeHub;

public class LaunchpadListener implements Listener {

    private DeluxeHub plugin;

    public LaunchpadListener(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfigUtil().getLaunchpads().getBoolean("enabled", true)) {
            return;
        }

        Player player = event.getPlayer();

        // Check if player moved to a new block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        // Check if player is standing on a pressure plate
        Material blockBelow = player.getLocation().subtract(0, 0.1, 0).getBlock().getType();

        if (isPressurePlate(blockBelow)
                || isLaunchpadLocation(player.getLocation().getBlock().getLocation())
                || isLaunchpadLocation(player.getLocation().subtract(0, 0.1, 0).getBlock().getLocation())) {
            launchPlayer(player);
        }
    }

    private boolean isPressurePlate(Material material) {
        List<String> plateTypes = plugin.getConfigUtil().getLaunchpads().getStringList("plate-types");
        return plateTypes.contains(material.name());
    }

    // New helper method for location check
    private boolean isLaunchpadLocation(org.bukkit.Location loc) {
        String locStr = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
                + loc.getBlockZ();
        return plugin.getConfig().getBoolean("launchpads.locations." + locStr, false);
    }

    private void launchPlayer(Player player) {
        double velocityMultiplier = plugin.getConfigUtil().getLaunchpadVelocityMultiplier();
        double heightMultiplier = plugin.getConfigUtil().getLaunchpadHeightMultiplier();

        // Get player's facing direction
        Vector direction = player.getLocation().getDirection();
        direction.setY(0); // Remove vertical component
        direction.normalize(); // Normalize to unit vector

        // Apply velocity
        Vector velocity = direction.multiply(velocityMultiplier);
        velocity.setY(heightMultiplier);

        player.setVelocity(velocity);

        // Play sound
        String soundName = plugin.getConfig().getString("launchpads.sound", "BAT_TAKEOFF");
        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Ignore invalid sound
        }

        // Spawn particles (1.8 compatible)
        try {
            // Simple particle effect at launch location
            player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.MOBSPAWNER_FLAMES, 0);
        } catch (Exception e) {
            // Ignore
        }
    }
}
