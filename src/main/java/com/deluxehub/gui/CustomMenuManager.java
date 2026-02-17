package com.deluxehub.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.deluxehub.DeluxeHub;

public class CustomMenuManager {

    private DeluxeHub plugin;
    private Map<String, CustomMenu> menus;

    public CustomMenuManager(DeluxeHub plugin) {
        this.plugin = plugin;
        this.menus = new HashMap<>();
        loadMenus();
    }

    private void loadMenus() {
        if (!plugin.getConfigUtil().isFeatureEnabled("custom-menus")) {
            return;
        }

        ConfigurationSection menusSection = plugin.getConfigUtil().getCustomMenusSection();
        if (menusSection == null) {
            return;
        }

        for (String menuName : menusSection.getKeys(false)) {
            ConfigurationSection menuSection = menusSection.getConfigurationSection(menuName);
            if (menuSection == null)
                continue;

            String title = menuSection.getString("title", "&8Custom Menu");
            int size = menuSection.getInt("size", 27);

            // Ensure size is a multiple of 9 and between 9 and 54
            if (size < 9)
                size = 9;
            if (size > 54)
                size = 54;
            size = (int) Math.ceil(size / 9.0) * 9;

            CustomMenu menu = new CustomMenu(plugin, menuName, title, size);

            // Load items
            ConfigurationSection itemsSection = menuSection.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String itemName : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                    if (itemSection == null)
                        continue;

                    String material = itemSection.getString("material", "STONE");
                    byte data = (byte) itemSection.getInt("data", 0);
                    String name = itemSection.getString("name", "&aItem");
                    List<String> lore = itemSection.getStringList("lore");
                    String command = itemSection.getString("command", "");
                    boolean closeOnClick = itemSection.getBoolean("close-on-click", true);
                    boolean consoleCommand = itemSection.getBoolean("console-command", false);

                    CustomMenu.MenuItem menuItem = new CustomMenu.MenuItem(
                            material, data, name, lore, command, closeOnClick, consoleCommand);

                    int slot = itemSection.getInt("slot", 0);
                    if (slot >= 0 && slot < size) {
                        menu.addItem(slot, menuItem);
                    }
                }
            }

            menus.put(menuName.toLowerCase(), menu);
            plugin.getServer().getPluginManager().registerEvents(menu, plugin);

            plugin.getLogger().info("Loaded custom menu '" + menuName + "' with " +
                    menu.getItems().size() + " items");
        }
    }

    public CustomMenu getMenu(String name) {
        return menus.get(name.toLowerCase());
    }

    public void openMenu(Player player, String menuName) {
        CustomMenu menu = getMenu(menuName);
        if (menu != null) {
            menu.open(player);
        } else {
            player.sendMessage(plugin.color("&cMenu '" + menuName + "' not found!"));
        }
    }

    public Map<String, CustomMenu> getMenus() {
        return new HashMap<>(menus);
    }

    public void reloadMenus() {
        // Unregister existing menu listeners
        for (CustomMenu menu : menus.values()) {
            InventoryClickEvent.getHandlerList().unregister(menu);
        }

        menus.clear();
        loadMenus();
    }
}
