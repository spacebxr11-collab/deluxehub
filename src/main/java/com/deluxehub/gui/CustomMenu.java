package com.deluxehub.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.PlaceholderUtil;

public class CustomMenu implements Listener {

    private DeluxeHub plugin;
    private String name;
    private String title;
    private int size;
    private Map<Integer, MenuItem> items;
    private PlaceholderUtil placeholderUtil;

    public CustomMenu(DeluxeHub plugin, String name, String title, int size) {
        this.plugin = plugin;
        this.name = name;
        this.title = title;
        this.size = size;
        this.items = new HashMap<>();
        this.placeholderUtil = new PlaceholderUtil(plugin);
    }

    public void addItem(int slot, MenuItem item) {
        items.put(slot, item);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size,
                placeholderUtil.setPlaceholders(player, title));

        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            MenuItem item = entry.getValue();
            ItemStack itemStack = createItemStack(player, item);
            if (itemStack != null) {
                inventory.setItem(entry.getKey(), itemStack);
            }
        }

        player.openInventory(inventory);
    }

    private ItemStack createItemStack(Player player, MenuItem item) {
        try {
            Material material = Material.valueOf(item.getMaterial().toUpperCase());
            ItemStack itemStack = new ItemStack(material);

            if (item.getData() > 0) {
                itemStack.setDurability((short) item.getData());
            }

            ItemMeta meta = itemStack.getItemMeta();

            String displayName = placeholderUtil.setPlaceholders(player, item.getName());
            meta.setDisplayName(displayName);

            if (item.getLore() != null && !item.getLore().isEmpty()) {
                meta.setLore(placeholderUtil.setPlaceholders(player, item.getLore()));
            }

            itemStack.setItemMeta(meta);
            return itemStack;

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create menu item: " + item.getName() + " - " + e.getMessage());
            return null;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!event.getInventory().getTitle().equals(
                placeholderUtil.setPlaceholders(player, title))) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        MenuItem item = items.get(event.getSlot());
        if (item != null && item.getCommand() != null) {
            String command = placeholderUtil.setPlaceholders(player, item.getCommand());

            if (item.isCloseOnClick()) {
                player.closeInventory();
            }

            if (item.isConsoleCommand()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else {
                player.chat("/" + command);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, MenuItem> getItems() {
        return new HashMap<>(items);
    }

    public static class MenuItem {
        private String material;
        private byte data;
        private String name;
        private java.util.List<String> lore;
        private String command;
        private boolean closeOnClick;
        private boolean consoleCommand;

        public MenuItem(String material, byte data, String name, java.util.List<String> lore,
                String command, boolean closeOnClick, boolean consoleCommand) {
            this.material = material;
            this.data = data;
            this.name = name;
            this.lore = lore;
            this.command = command;
            this.closeOnClick = closeOnClick;
            this.consoleCommand = consoleCommand;
        }

        public String getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }

        public String getName() {
            return name;
        }

        public java.util.List<String> getLore() {
            return lore;
        }

        public String getCommand() {
            return command;
        }

        public boolean isCloseOnClick() {
            return closeOnClick;
        }

        public boolean isConsoleCommand() {
            return consoleCommand;
        }
    }
}
