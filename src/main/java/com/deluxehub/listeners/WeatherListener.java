package com.deluxehub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.deluxehub.DeluxeHub;

public class WeatherListener implements Listener {
    
    private DeluxeHub plugin;
    
    public WeatherListener(DeluxeHub plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("weather-change")) {
            return;
        }
        
        // Cancel weather changes to keep it always clear
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
