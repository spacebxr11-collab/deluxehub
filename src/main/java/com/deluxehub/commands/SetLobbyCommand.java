package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.SpawnUtil;

public class SetLobbyCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    private SpawnUtil spawnUtil;
    
    public SetLobbyCommand(DeluxeHub plugin, SpawnUtil spawnUtil) {
        this.plugin = plugin;
        this.spawnUtil = spawnUtil;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("deluxehub.setlobby")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        spawnUtil.setSpawnLocation(player.getLocation());
        player.sendMessage(plugin.getMessage("lobby-set"));
        
        return true;
    }
}
