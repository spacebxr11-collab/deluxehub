package com.deluxehub.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.deluxehub.DeluxeHub;

public class ServerSelector implements Listener {

    private DeluxeHub plugin;
    private Inventory inventory;

    public ServerSelector(DeluxeHub plugin) {
        this.plugin = plugin;
        createInventory();
    }

    private void createInventory() {
        String title = plugin.color(plugin.getConfigUtil().getServerSelector().getString("title", "&8Select a Server"));
        int size = plugin.getConfigUtil().getServerSelector().getInt("size", 27);

        inventory = Bukkit.createInventory(null, size, title);

        // Load server items
        if (plugin.getConfigUtil().getServerSelector().isConfigurationSection("items")) {
            for (String serverKey : plugin.getConfigUtil().getServerSelector().getConfigurationSection("items")
                    .getKeys(false)) {
                if (serverKey.equals("title") || serverKey.equals("size")) {
                    continue;
                }

                addItem(serverKey);
            }
        }
    }

    private void addItem(String serverKey) {
        try {
            String path = "items." + serverKey;

            int slot = plugin.getConfigUtil().getServerSelector().getInt(path + ".slot", 0);
            String materialName = plugin.getConfigUtil().getServerSelector().getString(path + ".material");
            byte data = (byte) plugin.getConfigUtil().getServerSelector().getInt(path + ".data", 0);

            Material material = Material.valueOf(materialName.toUpperCase());
            ItemStack item = new ItemStack(material, 1, data);

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(plugin.color(plugin.getConfigUtil().getServerSelector().getString(path + ".name")));

            List<String> lore = plugin.color(plugin.getConfigUtil().getServerSelector().getStringList(path + ".lore"));
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }

            item.setItemMeta(meta);

            // Store command in item metadata (using lore for simplicity in 1.8)
            String command = plugin.getConfigUtil().getServerSelector().getString(path + ".command");
            if (command != null) {
                lore.add(ChatColor.GRAY + "CMD:" + command);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            inventory.setItem(slot, item);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to add server item: " + serverKey + " - " + e.getMessage());
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (!meta.hasLore()) {
            return;
        }

        // Find command in lore
        for (String line : meta.getLore()) {
            if (line.startsWith(ChatColor.GRAY + "CMD:")) {
                String command = line.substring((ChatColor.GRAY + "CMD:").length());
                player.chat("/" + command);
                player.closeInventory();
                break;
            }
        }
    }
}
