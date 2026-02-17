package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.gui.CustomMenuManager;

public class MenuCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    private CustomMenuManager menuManager;
    
    public MenuCommand(DeluxeHub plugin, CustomMenuManager menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("deluxehub.menu")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(plugin.color("&cUsage: /menu <name>"));
            player.sendMessage(plugin.color("&7Available menus:"));
            
            for (String menuName : menuManager.getMenus().keySet()) {
                if (player.hasPermission("deluxehub.menu." + menuName)) {
                    player.sendMessage(plugin.color("&7- " + menuName));
                }
            }
            return true;
        }
        
        String menuName = args[0];
        
        if (!player.hasPermission("deluxehub.menu." + menuName)) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        menuManager.openMenu(player, menuName);
        
        return true;
    }
}
