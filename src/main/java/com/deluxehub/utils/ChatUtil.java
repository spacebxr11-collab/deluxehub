package com.deluxehub.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.deluxehub.DeluxeHub;

public class ChatUtil implements Listener {

    private DeluxeHub plugin;
    private boolean chatLocked;

    public ChatUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        this.chatLocked = false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check if chat is locked
        if (chatLocked && !player.hasPermission("deluxehub.chat.bypass")) {
            player.sendMessage(plugin.getMessage("chat-locked"));
            event.setCancelled(true);
            return;
        }

        // Anti-swear filter
        if (plugin.getConfig().getBoolean("chat.anti-swear.enabled", true)) {
            List<String> blockedWords = plugin.getConfig().getStringList("chat.anti-swear.words");
            String replacement = plugin.getConfig().getString("chat.anti-swear.replacement", "****");
            boolean foundSwear = false;

            for (String word : blockedWords) {
                if (message.toLowerCase().contains(word.toLowerCase())) {
                    message = message.replaceAll("(?i)" + word, replacement);
                    foundSwear = true;
                }
            }

            if (foundSwear) {
                String warning = plugin.getMessage("chat.anti-swear.warning");
                if (!warning.isEmpty()) {
                    player.sendMessage(warning);
                }
                event.setMessage(message);
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfig().getBoolean("chat.command-blocker.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase().split(" ")[0];

        List<String> blockedCommands = plugin.getConfig().getStringList("chat.command-blocker.blocked-commands");
        String blockMessage = plugin.getMessage("chat.command-blocker.message");

        for (String blocked : blockedCommands) {
            if (command.equals(blocked.toLowerCase()) || command.equals("/" + blocked.toLowerCase())) {
                if (!player.hasPermission("deluxehub.command.bypass")) {
                    player.sendMessage(blockMessage);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    public void lockChat() {
        chatLocked = true;
        Bukkit.broadcastMessage(plugin.getMessage("chat-locked"));
    }

    public void unlockChat() {
        chatLocked = false;
        Bukkit.broadcastMessage(plugin.getMessage("chat-unlocked"));
    }

    public boolean isChatLocked() {
        return chatLocked;
    }

    public void clearChat() {
        int lines = plugin.getConfig().getInt("chat.clearchat.lines", 100);
        String clearMessage = plugin
                .color(plugin.getConfig().getString("chat.clearchat.message", "&aChat has been cleared!"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < lines; i++) {
                player.sendMessage("");
            }
            player.sendMessage(clearMessage);
        }
    }
}
