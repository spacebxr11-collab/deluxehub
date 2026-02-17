package com.deluxehub.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderUtil {

    private DeluxeHub plugin;
    private boolean placeholderAPIEnabled;

    public PlaceholderUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        this.placeholderAPIEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public String setPlaceholders(Player player, String text) {
        if (placeholderAPIEnabled && player != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }

        // Fallback placeholders for when PlaceholderAPI is not available
        if (text == null)
            return "";

        text = text.replace("%player_name%", player != null ? player.getName() : "Unknown");
        text = text.replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%server_name%", "DeluxeHub");
        text = text.replace("%player_ping%", player != null ? String.valueOf(getPing(player)) : "0");
        text = text.replace("%vault_rank%",
                player != null && player.hasPermission("deluxehub.vip") ? "VIP" : "Default");

        return plugin.color(text);
    }

    public List<String> setPlaceholders(Player player, List<String> textList) {
        if (textList == null)
            return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String line : textList) {
            result.add(setPlaceholders(player, line));
        }
        return result;
    }

    private int getPing(Player player) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) craftPlayer.getClass().getField("ping").get(craftPlayer);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }
}
