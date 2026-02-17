package com.deluxehub.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.deluxehub.DeluxeHub;
import com.deluxehub.gui.ServerSelector;
import com.deluxehub.utils.PlayerHiderUtil;
import com.deluxehub.utils.PlaceholderUtil;
import com.deluxehub.utils.ScoreboardUtil;
import com.deluxehub.utils.SpawnUtil;
import com.deluxehub.utils.TablistUtil;

public class JoinListener implements Listener {

    private DeluxeHub plugin;
    private Map<Player, PlayerHiderUtil> playerHiders;
    private PlaceholderUtil placeholderUtil;
    private ScoreboardUtil scoreboardUtil;
    private TablistUtil tablistUtil;
    private SpawnUtil spawnUtil;

    public JoinListener(DeluxeHub plugin) {
        this.plugin = plugin;
        this.playerHiders = new HashMap<>();
        this.placeholderUtil = new PlaceholderUtil(plugin);
        this.scoreboardUtil = new ScoreboardUtil(plugin);
        this.tablistUtil = new TablistUtil(plugin);
        this.spawnUtil = new SpawnUtil(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Handle join messages
        if (plugin.getConfigUtil().isJoinFeatureEnabled("messages")) {
            String joinMessage = plugin.getConfig().getString("join.messages.join",
                    "&a%player_name% has joined the hub!");
            joinMessage = placeholderUtil.setPlaceholders(player, joinMessage);
            event.setJoinMessage(joinMessage);
        } else {
            event.setJoinMessage(null);
        }

        // Teleport to spawn if enabled
        if (spawnUtil.shouldTeleportOnJoin()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                spawnUtil.teleportToSpawn(player);
            }, 5L);
        }

        // Set health and hunger to max
        player.setHealth(20.0);
        player.setFoodLevel(20);

        // Clear inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // Give join items
        giveJoinItems(player);

        // Join MOTD
        if (plugin.getConfigUtil().isJoinFeatureEnabled("motd")) {
            List<String> motdLines = plugin.getConfig().getStringList("join.motd.lines");
            for (String line : motdLines) {
                player.sendMessage(placeholderUtil.setPlaceholders(player, line));
            }
        }

        // Join title
        if (plugin.getConfigUtil().isJoinFeatureEnabled("title")) {
            sendTitle(player);
        }

        // Join sound
        if (plugin.getConfigUtil().isJoinFeatureEnabled("sound")) {
            playJoinSound(player);
        }

        // Join firework
        if (plugin.getConfigUtil().isJoinFeatureEnabled("firework")) {
            // Delay for chunk loading, then spawn fireworks for 5 seconds
            new org.bukkit.scheduler.BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    if (count >= 5 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    spawnFirework(player);
                    count++;
                }
            }.runTaskTimer(plugin, 40L, 20L); // Start after 2 seconds, run every second
        }

        // Send welcome message
        if (plugin.getConfig().getString("messages.join") != null) {
            player.sendMessage(plugin.getMessage("join"));
        }

        // Send credits message (user requested chat instead of action bar)
        if (plugin.getConfigUtil().isActionBarEnabled()) {
            String message = plugin.getConfigUtil().getActionBarMessage();
            if (message != null && !message.isEmpty()) {
                player.sendMessage(plugin.color(message));
            }
        }

        // Set scoreboard
        if (plugin.getConfigUtil().isFeatureEnabled("scoreboard")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                scoreboardUtil.setScoreboard(player);
            }, 10L);
        }

        // Set tablist
        if (plugin.getConfigUtil().isFeatureEnabled("tablist")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                tablistUtil.setTablist(player);
            }, 15L);
        }

        // Initialize player hider
        if (plugin.getConfigUtil().isFeatureEnabled("player-hider")) {
            playerHiders.put(player, new PlayerHiderUtil(player));
        }

        // Update vanish for new player
        if (plugin.getVanishCommand() != null) {
            plugin.getVanishCommand().updateVanishForNewPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();

        // Handle compass (server selector)
        if (item.getType() == Material.COMPASS &&
                displayName.equals(plugin.color(plugin.getConfigUtil().getJoinItems().getString("compass.name")))) {
            event.setCancelled(true);
            new ServerSelector(plugin).open(player);
        }

        // Handle player hider
        if (plugin.getConfigUtil().isFeatureEnabled("player-hider") &&
                item.getType() == Material.EYE_OF_ENDER &&
                displayName
                        .equals(plugin.color(plugin.getConfigUtil().getJoinItems().getString("player-hider.name")))) {
            event.setCancelled(true);
            PlayerHiderUtil hider = playerHiders.get(player);
            if (hider != null) {
                hider.toggle();
                updatePlayerHiderItem(player, hider.isHidden());
            }
        }
    }

    private void giveJoinItems(Player player) {
        // Give compass
        if (plugin.getConfigUtil().getJoinItems().getBoolean("compass.enabled", true)) {
            ItemStack compass = createJoinItem("compass");
            if (compass != null) {
                player.getInventory().setItem(plugin.getConfigUtil().getJoinItems().getInt("compass.slot", 0), compass);
            }
        }

        // Give player hider
        if (plugin.getConfigUtil().isFeatureEnabled("player-hider") &&
                plugin.getConfigUtil().getJoinItems().getBoolean("player-hider.enabled", true)) {
            ItemStack hider = createJoinItem("player-hider");
            if (hider != null) {
                player.getInventory().setItem(plugin.getConfigUtil().getJoinItems().getInt("player-hider.slot", 1),
                        hider);
            }
        }
    }

    private ItemStack createJoinItem(String itemPath) {
        try {
            String materialName = plugin.getConfigUtil().getJoinItems().getString(itemPath + ".material");
            Material material = Material.valueOf(materialName.toUpperCase());

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(plugin.color(plugin.getConfigUtil().getJoinItems().getString(itemPath + ".name")));

            // Set lore if available
            if (plugin.getConfigUtil().getJoinItems().contains(itemPath + ".lore")) {
                meta.setLore(plugin.color(plugin.getConfigUtil().getJoinItems().getStringList(itemPath + ".lore")));
            }

            item.setItemMeta(meta);
            return item;

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create join item: " + itemPath + " - " + e.getMessage());
            return null;
        }
    }

    private void updatePlayerHiderItem(Player player, boolean hidden) {
        ItemStack item = player.getInventory()
                .getItem(plugin.getConfigUtil().getJoinItems().getInt("player-hider.slot", 1));
        if (item == null || item.getType() != Material.EYE_OF_ENDER) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.color(plugin.getConfigUtil().getJoinItems().getString("player-hider.name")));

        // Update lore
        java.util.List<String> lore = plugin
                .color(plugin.getConfigUtil().getJoinItems().getStringList("player-hider.lore"));
        if (lore.size() >= 2) {
            lore.set(1, "&7Current: " + (hidden ? "&cHidden" : "&aVisible"));
        }
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    public Map<Player, PlayerHiderUtil> getPlayerHiders() {
        return playerHiders;
    }

    private void sendTitle(Player player) {
        try {
            String title = placeholderUtil.setPlaceholders(player,
                    plugin.getConfig().getString("join.title.title", "&6Welcome!"));
            String subtitle = placeholderUtil.setPlaceholders(player,
                    plugin.getConfig().getString("join.title.subtitle", "&7Enjoy your stay!"));

            int fadeIn = plugin.getConfig().getInt("join.title.fade-in", 20);
            int stay = plugin.getConfig().getInt("join.title.stay", 60);
            int fadeOut = plugin.getConfig().getInt("join.title.fade-out", 20);

            Object enumTitle = com.deluxehub.utils.ReflectionUtil.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                    .getField("TITLE").get(null);
            Object enumSubtitle = com.deluxehub.utils.ReflectionUtil.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                    .getField("SUBTITLE").get(null);

            Object titleChat = com.deluxehub.utils.ReflectionUtil.getNMSClass("IChatBaseComponent$ChatSerializer")
                    .getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
            Object subtitleChat = com.deluxehub.utils.ReflectionUtil.getNMSClass("IChatBaseComponent$ChatSerializer")
                    .getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");

            java.lang.reflect.Constructor<?> titleConstructor = com.deluxehub.utils.ReflectionUtil
                    .getNMSClass("PacketPlayOutTitle").getConstructor(
                            com.deluxehub.utils.ReflectionUtil.getNMSClass("PacketPlayOutTitle$EnumTitleAction"),
                            com.deluxehub.utils.ReflectionUtil.getNMSClass("IChatBaseComponent"),
                            int.class, int.class, int.class);

            Object titlePacket = titleConstructor.newInstance(enumTitle, titleChat, fadeIn, stay, fadeOut);
            Object subtitlePacket = titleConstructor.newInstance(enumSubtitle, subtitleChat, fadeIn, stay, fadeOut);

            com.deluxehub.utils.ReflectionUtil.sendPacket(player, titlePacket);
            com.deluxehub.utils.ReflectionUtil.sendPacket(player, subtitlePacket);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send title: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playJoinSound(Player player) {
        try {
            String soundName = plugin.getConfig().getString("join.sound.sound", "LEVEL_UP");
            float volume = (float) plugin.getConfig().getDouble("join.sound.volume", 1.0);
            float pitch = (float) plugin.getConfig().getDouble("join.sound.pitch", 1.0);

            Sound sound;
            try {
                sound = Sound.valueOf(soundName);
            } catch (IllegalArgumentException e) {
                sound = Sound.LEVEL_UP; // Fallback for 1.8.9
            }

            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to play join sound: " + e.getMessage());
        }
    }

    private void spawnFirework(Player player) {
        try {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();

            int power = plugin.getConfig().getInt("join.firework.power", 1);
            meta.setPower(power);

            List<String> colorNames = plugin.getConfig().getStringList("join.firework.colors");
            if (colorNames.isEmpty()) {
                colorNames.add("RED");
                colorNames.add("GREEN");
                colorNames.add("BLUE");
            }

            FireworkEffect.Builder builder = FireworkEffect.builder();
            builder.with(FireworkEffect.Type.BALL);

            for (String colorName : colorNames) {
                try {
                    Color color = getColor(colorName.toUpperCase());
                    if (color != null) {
                        builder.withColor(color);
                    }
                } catch (Exception e) {
                    // Skip invalid colors
                }
            }

            meta.addEffect(builder.build());
            firework.setFireworkMeta(meta);

            // Remove firework after explosion
            Bukkit.getScheduler().runTaskLater(plugin, firework::remove, 30L);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to spawn firework: " + e.getMessage());
        }
    }

    private Color getColor(String name) {
        switch (name) {
            case "WHITE":
                return Color.WHITE;
            case "SILVER":
                return Color.SILVER;
            case "GRAY":
                return Color.GRAY;
            case "BLACK":
                return Color.BLACK;
            case "RED":
                return Color.RED;
            case "MAROON":
                return Color.MAROON;
            case "YELLOW":
                return Color.YELLOW;
            case "OLIVE":
                return Color.OLIVE;
            case "LIME":
                return Color.LIME;
            case "GREEN":
                return Color.GREEN;
            case "AQUA":
                return Color.AQUA;
            case "TEAL":
                return Color.TEAL;
            case "BLUE":
                return Color.BLUE;
            case "NAVY":
                return Color.NAVY;
            case "FUCHSIA":
                return Color.FUCHSIA;
            case "PURPLE":
                return Color.PURPLE;
            case "ORANGE":
                return Color.ORANGE;
            default:
                return null;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Handle quit messages
        if (plugin.getConfigUtil().isJoinFeatureEnabled("messages")) {
            String quitMessage = plugin.getConfig().getString("join.messages.quit",
                    "&c%player_name% has left the hub!");
            quitMessage = placeholderUtil.setPlaceholders(player, quitMessage);
            event.setQuitMessage(quitMessage);
        } else {
            event.setQuitMessage(null);
        }

        // Clean up player hider
        if (playerHiders.containsKey(player)) {
            playerHiders.remove(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (spawnUtil.shouldTeleportOnRespawn()) {
            event.setRespawnLocation(spawnUtil.getSpawnLocation());
        }
    }
}
