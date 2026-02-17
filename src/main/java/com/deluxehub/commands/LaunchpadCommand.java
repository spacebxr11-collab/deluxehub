package com.deluxehub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.Set;
import com.deluxehub.DeluxeHub;

public class LaunchpadCommand implements CommandExecutor {

    private DeluxeHub plugin;

    public LaunchpadCommand(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("deluxehub.launchpad")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.color("&cUsage: /launchpad <create|list|remove>"));
            return true;
        }

        if (!(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(plugin.color("&cOnly players can use this command."));
            return true;
        }

        org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                org.bukkit.Location loc = player.getLocation().getBlock().getLocation();
                String locStr = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
                        + loc.getBlockZ();

                if (plugin.getConfig().getBoolean("launchpads.locations." + locStr)) {
                    sender.sendMessage(plugin.color("&cA launchpad already exists at this location!"));
                } else {
                    plugin.getConfig().set("launchpads.locations." + locStr, true);
                    plugin.saveConfig();
                    sender.sendMessage(plugin.color("&aLaunchpad created at your location!"));
                }
                break;

            case "remove":
                loc = player.getLocation().getBlock().getLocation();
                locStr = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
                        + loc.getBlockZ();

                if (!plugin.getConfig().getBoolean("launchpads.locations." + locStr)) {
                    // Try checking block below if not found at feet
                    loc = player.getLocation().subtract(0, 1, 0).getBlock().getLocation();
                    locStr = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
                            + loc.getBlockZ();
                }

                if (plugin.getConfig().getBoolean("launchpads.locations." + locStr)) {
                    plugin.getConfig().set("launchpads.locations." + locStr, null);
                    plugin.saveConfig();
                    sender.sendMessage(plugin.color("&aLaunchpad removed!"));
                } else {
                    sender.sendMessage(plugin.color("&cNo launchpad found at your location or below you."));
                }
                break;

            case "list":
                if (plugin.getConfig().getConfigurationSection("launchpads.locations") == null) {
                    sender.sendMessage(plugin.color("&7No launchpads set."));
                } else {
                    Set<String> keys = plugin.getConfig().getConfigurationSection("launchpads.locations")
                            .getKeys(false);
                    sender.sendMessage(plugin.color("&aLaunchpads (" + keys.size() + "):"));
                    for (String key : keys) {
                        sender.sendMessage(plugin.color("&7- " + key));
                    }
                }
                break;

            default:
                sender.sendMessage(plugin.color("&cUsage: /launchpad <create|list|remove>"));
                break;
        }

        return true;
    }
}
