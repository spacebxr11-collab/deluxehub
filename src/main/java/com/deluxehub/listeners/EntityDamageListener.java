package com.deluxehub.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.deluxehub.DeluxeHub;

public class EntityDamageListener implements Listener {
    
    private DeluxeHub plugin;
    
    public EntityDamageListener(DeluxeHub plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("entity-damage")) {
            return;
        }
        
        Entity entity = event.getEntity();
        
        // Allow damage if entity has permission or is not a player
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!player.hasPermission("deluxehub.admin")) {
                event.setCancelled(true);
            }
        }
    }
}
