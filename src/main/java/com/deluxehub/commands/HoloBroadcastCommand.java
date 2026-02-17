package com.deluxehub.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.deluxehub.DeluxeHub;
import com.deluxehub.holograms.Hologram;

import java.util.Arrays;

public class HoloBroadcastCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public HoloBroadcastCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deluxehub.holobroadcast")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.color("&cUsage: /holobroadcast <message>"));
            return true;
        }

        String message = String.join(" ", args);
        doHoloBroadcast(player, message);

        return true;
    }

    private void doHoloBroadcast(Player player, String message) {
        // Calculate position in front of player
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));

        final Hologram hologram = new Hologram(plugin, loc, Arrays.asList(plugin.color(message)));
        hologram.spawn(player);

        // Remove after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                hologram.destroy(player);
            }
        }.runTaskLater(plugin, 100L); // 5 seconds
    }
}
