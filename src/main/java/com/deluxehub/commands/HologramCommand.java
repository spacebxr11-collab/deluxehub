package com.deluxehub.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.holograms.Hologram;

public class HologramCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public HologramCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deluxehub.hologram")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.color("&cUsage: /hologram <create|remove|list> [text]"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(plugin.color("&cUsage: /hologram create <text>"));
                    return true;
                }
                String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                List<String> lines = Arrays.asList(text.split("\\\\n"));
                plugin.getHologramManager().createHologram(player.getLocation(), lines);
                player.sendMessage(plugin.color("&aHologram created!"));
                break;
            case "remove":
                // Basic removal of nearest hologram
                Hologram nearest = null;
                double dist = 5.0;
                for (Hologram holo : plugin.getHologramManager().getHolograms()) {
                    if (holo.getLocation().getWorld().equals(player.getWorld())) {
                        double d = holo.getLocation().distance(player.getLocation());
                        if (d < dist) {
                            dist = d;
                            nearest = holo;
                        }
                    }
                }
                if (nearest != null) {
                    plugin.getHologramManager().removeHologram(nearest);
                    player.sendMessage(plugin.color("&aNearest hologram removed!"));
                } else {
                    player.sendMessage(plugin.color("&cNo hologram found nearby!"));
                }
                break;
            case "list":
                player.sendMessage(plugin.color("&6Holograms: " + plugin.getHologramManager().getHolograms().size()));
                break;
            default:
                player.sendMessage(plugin.color("&cInvalid subcommand!"));
                break;
        }

        return true;
    }
}
