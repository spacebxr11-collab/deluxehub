package com.deluxehub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.deluxehub.commands.ClearChatCommand;
import com.deluxehub.commands.FlyCommand;
import com.deluxehub.commands.GamemodeCommand;
import com.deluxehub.commands.HubCommand;
import com.deluxehub.commands.LobbyCommand;
import com.deluxehub.commands.LockChatCommand;
import com.deluxehub.commands.MenuCommand;
import com.deluxehub.commands.ServerCommand;
import com.deluxehub.commands.SetLobbyCommand;
import com.deluxehub.commands.VanishCommand;
import com.deluxehub.commands.AnnounceCommand;
import com.deluxehub.commands.HiderCommand;
import com.deluxehub.commands.HologramCommand;
import com.deluxehub.commands.SetJoinItemCommand;
import com.deluxehub.gui.CustomMenuManager;
import com.deluxehub.holograms.HologramManager;
import com.deluxehub.listeners.AntiWorldDownloaderListener;
import com.deluxehub.listeners.AnnouncementListener;
import com.deluxehub.listeners.DoubleJumpListener;
import com.deluxehub.listeners.LaunchpadListener;
import com.deluxehub.listeners.ProtectionListener;
import com.deluxehub.listeners.WorldProtectionListener;
import com.deluxehub.listeners.JoinListener;
import com.deluxehub.utils.ActionBarUtil;
import com.deluxehub.utils.ChatUtil;
import com.deluxehub.utils.ConfigUtil;
import com.deluxehub.utils.PlaceholderUtil;
import com.deluxehub.utils.ScoreboardUtil;
import com.deluxehub.utils.SpawnUtil;
import com.deluxehub.utils.TablistUtil;

public class DeluxeHub extends JavaPlugin {

    private static DeluxeHub instance;
    private Logger logger;
    private ConfigUtil configUtil;
    private ActionBarUtil actionBarUtil;
    private PlaceholderUtil placeholderUtil;
    private ScoreboardUtil scoreboardUtil;
    private TablistUtil tablistUtil;
    private SpawnUtil spawnUtil;
    private ChatUtil chatUtil;
    private HologramManager hologramManager;
    private CustomMenuManager customMenuManager;
    private AnnouncementListener announcementListener;
    private VanishCommand vanishCommand;
    private java.util.Set<java.util.UUID> flyingPlayers;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        flyingPlayers = new java.util.HashSet<>();

        // Save default config
        saveDefaultConfig();

        // Initialize utilities
        configUtil = new ConfigUtil(this);
        actionBarUtil = new ActionBarUtil(this);
        placeholderUtil = new PlaceholderUtil(this);
        scoreboardUtil = new ScoreboardUtil(this);
        tablistUtil = new TablistUtil(this);
        spawnUtil = new SpawnUtil(this);
        chatUtil = new ChatUtil(this);
        hologramManager = new HologramManager(this);
        customMenuManager = new CustomMenuManager(this);
        vanishCommand = new VanishCommand(this);

        // Register commands
        getCommand("hub").setExecutor(new HubCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("server").setExecutor(new ServerCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("gm").setExecutor(new GamemodeCommand(this));
        getCommand("lockchat").setExecutor(new LockChatCommand(this, chatUtil));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this, chatUtil));
        getCommand("setlobby").setExecutor(new SetLobbyCommand(this, spawnUtil));
        getCommand("lobby").setExecutor(new LobbyCommand(this, spawnUtil));
        getCommand("vanish").setExecutor(vanishCommand);
        getCommand("menu").setExecutor(new MenuCommand(this, customMenuManager));
        getCommand("announce").setExecutor(new AnnounceCommand(this));
        getCommand("hider").setExecutor(new HiderCommand(this));
        getCommand("hologram").setExecutor(new HologramCommand(this));
        getCommand("setjoinitem").setExecutor(new SetJoinItemCommand(this));
        getCommand("holobroadcast").setExecutor(new com.deluxehub.commands.HoloBroadcastCommand(this));

        // Register listeners
        if (configUtil.isFeatureEnabled("join-management")) {
            getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        }

        if (configUtil.isFeatureEnabled("world-protection")) {
            getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
            getServer().getPluginManager().registerEvents(new WorldProtectionListener(this, spawnUtil), this);
        }

        if (configUtil.isFeatureEnabled("launchpads")) {
            getServer().getPluginManager().registerEvents(new LaunchpadListener(this), this);
        }

        if (configUtil.isFeatureEnabled("double-jump")) {
            getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        }

        if (configUtil.isFeatureEnabled("anti-world-downloader")) {
            getServer().getPluginManager().registerEvents(new AntiWorldDownloaderListener(this), this);
        }

        // Register utility listeners
        getServer().getPluginManager().registerEvents(chatUtil, this);

        // Initialize announcements
        announcementListener = new AnnouncementListener(this);

        // Setup scoreboard and tablist for online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (configUtil.isFeatureEnabled("scoreboard")) {
                scoreboardUtil.setScoreboard(player);
            }
            if (configUtil.isFeatureEnabled("tablist")) {
                tablistUtil.setTablist(player);
            }
        }

        // Start periodic tasks
        startPeriodicTasks();

        logger.info(ChatColor.GREEN + "DeluxeHub has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Stop announcements
        if (announcementListener != null) {
            announcementListener.stopAnnouncements();
        }

        // Destroy holograms
        if (hologramManager != null) {
            hologramManager.destroyAllHolograms();
        }

        logger.info(ChatColor.RED + "DeluxeHub has been disabled!");
    }

    private void startPeriodicTasks() {
        // Scoreboard update task
        if (configUtil.isFeatureEnabled("scoreboard")) {
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    scoreboardUtil.updateScoreboard(player);
                }
            }, 0L, getConfig().getInt("scoreboard.update-interval", 20) * 1L);
        }

        // Tablist update task
        if (configUtil.isFeatureEnabled("tablist")) {
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tablistUtil.setTablist(player);
                }
            }, 0L, 100L); // Update every 5 seconds
        }

        // Hologram update task
        if (configUtil.isFeatureEnabled("holograms")) {
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                hologramManager.updateAllHolograms();
            }, 0L, 200L); // Update every 10 seconds
        }
    }

    public static DeluxeHub getInstance() {
        return instance;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public ActionBarUtil getActionBarUtil() {
        return actionBarUtil;
    }

    public PlaceholderUtil getPlaceholderUtil() {
        return placeholderUtil;
    }

    public ScoreboardUtil getScoreboardUtil() {
        return scoreboardUtil;
    }

    public TablistUtil getTablistUtil() {
        return tablistUtil;
    }

    public SpawnUtil getSpawnUtil() {
        return spawnUtil;
    }

    public ChatUtil getChatUtil() {
        return chatUtil;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public VanishCommand getVanishCommand() {
        return vanishCommand;
    }

    public CustomMenuManager getCustomMenuManager() {
        return customMenuManager;
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> color(List<String> messages) {
        if (messages == null)
            return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String message : messages) {
            result.add(color(message));
        }
        return result;
    }

    public String getMessage(String path) {
        String message = getConfig().getString("messages." + path);
        return message != null ? color(message) : color("&cMessage not found: " + path);
    }

    public boolean hasFlightMode(Player player) {
        return flyingPlayers.contains(player.getUniqueId());
    }

    public void setFlightMode(Player player, boolean flying) {
        if (flying) {
            flyingPlayers.add(player.getUniqueId());
        } else {
            flyingPlayers.remove(player.getUniqueId());
        }
    }
}
