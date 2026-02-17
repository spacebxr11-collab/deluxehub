package com.deluxehub.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

public class VanishCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    private Map<Player, Boolean> vanishedPlayers;
    
    public VanishCommand(DeluxeHub plugin) {
        this.plugin = plugin;
        this.vanishedPlayers = new HashMap<>();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("deluxehub.vanish")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        if (isVanished(player)) {
            unvanishPlayer(player);
        } else {
            vanishPlayer(player);
        }
        
        return true;
    }
    
    public void vanishPlayer(Player player) {
        vanishedPlayers.put(player, true);
        
        // Hide player from all other players
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (!other.equals(player) && !other.hasPermission("deluxehub.vanish.see")) {
                other.hidePlayer(player);
            }
        }
        
        player.sendMessage(plugin.getMessage("vanish-enabled"));
    }
    
    public void unvanishPlayer(Player player) {
        vanishedPlayers.put(player, false);
        
        // Show player to all other players
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (!other.equals(player)) {
                other.showPlayer(player);
            }
        }
        
        player.sendMessage(plugin.getMessage("vanish-disabled"));
    }
    
    public boolean isVanished(Player player) {
        return vanishedPlayers.getOrDefault(player, false);
    }
    
    public void updateVanishForNewPlayer(Player newPlayer) {
        for (Player vanished : vanishedPlayers.keySet()) {
            if (isVanished(vanished) && !newPlayer.hasPermission("deluxehub.vanish.see")) {
                newPlayer.hidePlayer(vanished);
            }
        }
    }
}
