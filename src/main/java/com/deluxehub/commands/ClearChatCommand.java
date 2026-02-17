package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.ChatUtil;

public class ClearChatCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    private ChatUtil chatUtil;
    
    public ClearChatCommand(DeluxeHub plugin, ChatUtil chatUtil) {
        this.plugin = plugin;
        this.chatUtil = chatUtil;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("deluxehub.chat.clear")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        chatUtil.clearChat();
        
        return true;
    }
}
