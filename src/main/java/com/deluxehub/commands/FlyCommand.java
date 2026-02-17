package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

public class FlyCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public FlyCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deluxehub.fly")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.setFlightMode(player, false);
            player.sendMessage(plugin.getMessage("fly-disabled"));
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            plugin.setFlightMode(player, true);
            player.sendMessage(plugin.getMessage("fly-enabled"));
        }

        return true;
    }
}
