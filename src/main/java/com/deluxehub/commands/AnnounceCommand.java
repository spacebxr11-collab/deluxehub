package com.deluxehub.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deluxehub.DeluxeHub;

public class AnnounceCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public AnnounceCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("deluxehub.announce")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.color("&cUsage: /announce <message>"));
            return true;
        }

        String message = String.join(" ", args);
        String announcement = plugin
                .color(plugin.getConfig().getString("messages.announce-prefix", "&6&l[&eANNOUNCE&6&l] &r")) + message;

        Bukkit.broadcastMessage(announcement);
        return true;
    }
}
