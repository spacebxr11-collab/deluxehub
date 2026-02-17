package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.PlayerHiderUtil;

public class HiderCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public HiderCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deluxehub.hider")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        // This command toggles visibility using PlayerHiderUtil
        // Similar logic to what's in JoinListener for the item interaction
        PlayerHiderUtil hider = new PlayerHiderUtil(player);
        hider.toggle();

        if (hider.isHidden()) {
            player.sendMessage(plugin.color("&aAll players are now hidden!"));
        } else {
            player.sendMessage(plugin.color("&aPlayers are now visible!"));
        }

        return true;
    }
}
