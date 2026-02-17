package com.deluxehub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.deluxehub.DeluxeHub;

public class AntiWorldDownloaderListener implements Listener, PluginMessageListener {
    
    private DeluxeHub plugin;
    
    public AntiWorldDownloaderListener(DeluxeHub plugin) {
        this.plugin = plugin;
        
        if (plugin.getConfigUtil().isFeatureEnabled("anti-world-downloader")) {
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "WDL|INIT", this);
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "wdl-init", this);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigUtil().isFeatureEnabled("anti-world-downloader")) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Check for common World Downloader mods by checking plugin channels
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                // Send a test packet to detect World Downloader
                player.sendPluginMessage(plugin, "WDL|CONTROL", new byte[]{0});
            } catch (Exception e) {
                // Ignore exceptions
            }
        }, 20L);
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!plugin.getConfigUtil().isFeatureEnabled("anti-world-downloader")) {
            return;
        }
        
        // Check for World Downloader channels
        if (channel.equals("WDL|INIT") || channel.equals("wdl-init")) {
            String kickMessage = plugin.color(plugin.getConfig().getString("anti-world-downloader.kick-message", 
                "&cWorld Downloaders are not allowed!"));
            player.kickPlayer(kickMessage);
            
            plugin.getLogger().warning("Player " + player.getName() + " was kicked for using World Downloader!");
        }
    }
}
