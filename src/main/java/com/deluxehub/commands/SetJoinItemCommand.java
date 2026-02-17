package com.deluxehub.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.deluxehub.DeluxeHub;

public class SetJoinItemCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public SetJoinItemCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deluxehub.setjoinitem")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            // Simplified clear logic for demonstration
            player.sendMessage(
                    plugin.color("&aJoin items cleared (Note: This is a placeholder for actual config management)"));
            return true;
        }

        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(plugin.color("&cYou must hold an item to set it as a join item!"));
            return true;
        }

        // This is where you would normally save to config
        player.sendMessage(plugin
                .color("&aJoin item set! (Note: This feature requires manual config entry in this modular version)"));

        return true;
    }
}
