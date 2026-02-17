package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.ChatUtil;

public class LockChatCommand implements CommandExecutor {

    private DeluxeHub plugin;
    private ChatUtil chatUtil;

    public LockChatCommand(DeluxeHub plugin, ChatUtil chatUtil) {
        this.plugin = plugin;
        this.chatUtil = chatUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("deluxehub.chat.lock")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (chatUtil.isChatLocked()) {
            chatUtil.unlockChat();
        } else {
            chatUtil.lockChat();
        }

        return true;
    }
}
