package com.deluxehub.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.PlaceholderUtil;

public class AnnouncementListener {
    
    private DeluxeHub plugin;
    private PlaceholderUtil placeholderUtil;
    private BukkitRunnable announcementTask;
    private int currentIndex;
    
    public AnnouncementListener(DeluxeHub plugin) {
        this.plugin = plugin;
        this.placeholderUtil = new PlaceholderUtil(plugin);
        this.currentIndex = 0;
        
        if (plugin.getConfigUtil().isFeatureEnabled("announcements")) {
            startAnnouncements();
        }
    }
    
    private void startAnnouncements() {
        int interval = plugin.getConfig().getInt("announcements.interval", 300) * 20; // Convert to ticks
        List<String> messages = plugin.getConfig().getStringList("announcements.messages");
        
        if (messages.isEmpty()) {
            return;
        }
        
        announcementTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (messages.isEmpty()) {
                    return;
                }
                
                String message = messages.get(currentIndex);
                Bukkit.broadcastMessage(placeholderUtil.setPlaceholders(null, message));
                
                currentIndex = (currentIndex + 1) % messages.size();
            }
        };
        
        announcementTask.runTaskTimer(plugin, interval, interval);
    }
    
    public void stopAnnouncements() {
        if (announcementTask != null) {
            announcementTask.cancel();
            announcementTask = null;
        }
    }
    
    public void restartAnnouncements() {
        stopAnnouncements();
        if (plugin.getConfigUtil().isFeatureEnabled("announcements")) {
            startAnnouncements();
        }
    }
}
