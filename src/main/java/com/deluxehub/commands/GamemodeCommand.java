package com.deluxehub.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

public class GamemodeCommand implements CommandExecutor {
    
    private DeluxeHub plugin;
    
    public GamemodeCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("deluxehub.gamemode")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(plugin.color("&cUsage: /gamemode <mode>"));
            return true;
        }
        
        GameMode gameMode = null;
        String mode = args[0].toLowerCase();
        
        switch (mode) {
            case "0":
            case "survival":
            case "s":
                gameMode = GameMode.SURVIVAL;
                break;
            case "1":
            case "creative":
            case "c":
                gameMode = GameMode.CREATIVE;
                break;
            case "2":
            case "adventure":
            case "a":
                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
            case "spectator":
            case "sp":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                player.sendMessage(plugin.color("&cInvalid gamemode! Use: survival, creative, adventure, or spectator"));
                return true;
        }
        
        player.setGameMode(gameMode);
        player.sendMessage(plugin.getMessage("gamemode-set").replace("%gamemode%", gameMode.name().toLowerCase()));
        
        return true;
    }
}
