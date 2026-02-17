package com.deluxehub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.deluxehub.DeluxeHub;

public class ProtectionListener implements Listener {
    
    private DeluxeHub plugin;
    
    public ProtectionListener(DeluxeHub plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("block-break")) {
            return;
        }
        
        if (!event.getPlayer().hasPermission("deluxehub.admin")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("block-place")) {
            return;
        }
        
        if (!event.getPlayer().hasPermission("deluxehub.admin")) {
            event.setCancelled(true);
        }
    }
}
