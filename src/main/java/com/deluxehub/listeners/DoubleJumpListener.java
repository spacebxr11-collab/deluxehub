package com.deluxehub.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.deluxehub.DeluxeHub;

public class DoubleJumpListener implements Listener {

    private DeluxeHub plugin;

    public DoubleJumpListener(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfigUtil().isFeatureEnabled("double-jump")) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR
                && !plugin.hasFlightMode(player)) {
            if (player.getLocation().subtract(0, 0.1, 0).getBlock().getType() != Material.AIR) {
                player.setAllowFlight(true);
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!plugin.getConfigUtil().isFeatureEnabled("double-jump")) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR
                || plugin.hasFlightMode(player)) {
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        // Calculate jump velocity
        double multiplier = plugin.getConfig().getDouble("double-jump.multiplier", 1.5);
        double height = plugin.getConfig().getDouble("double-jump.height", 1.2);

        Vector direction = player.getLocation().getDirection().multiply(multiplier);
        direction.setY(height);

        player.setVelocity(direction);

        // Play sound if enabled
        if (plugin.getConfig().getBoolean("double-jump.sound.enabled", true)) {
            String soundName = plugin.getConfig().getString("double-jump.sound", "ENTITY_BAT_TAKEOFF");
            try {
                player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(soundName), 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                // Fallback sound for 1.8.9
                player.playSound(player.getLocation(), org.bukkit.Sound.BAT_TAKEOFF, 1.0f, 1.0f);
            }
        }
    }
}
