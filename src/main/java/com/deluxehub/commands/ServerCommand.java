package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.gui.ServerSelector;

public class ServerCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    
    public ServerCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getConfigUtil().isFeatureEnabled("server-selector")) {
            player.sendMessage(plugin.color("&cServer selector is disabled!"));
            return true;
        }
        
        new ServerSelector(plugin).open(player);
        
        return true;
    }
}
