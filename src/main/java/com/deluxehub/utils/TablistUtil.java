package com.deluxehub.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;

public class TablistUtil {

    private DeluxeHub plugin;
    private PlaceholderUtil placeholderUtil;

    public TablistUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        this.placeholderUtil = new PlaceholderUtil(plugin);
    }

    public void setTablist(Player player) {
        if (!plugin.getConfigUtil().isFeatureEnabled("tablist")) {
            return;
        }

        List<String> headerList = plugin.getConfig().getStringList("tablist.header");
        List<String> footerList = plugin.getConfig().getStringList("tablist.footer");

        String header = String.join("\n", headerList);
        String footer = String.join("\n", footerList);

        header = placeholderUtil.setPlaceholders(player, header);
        footer = placeholderUtil.setPlaceholders(player, footer);

        // Use reflection for 1.8.9 tablist
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Class<?> packetClass = getNMSClass("PacketPlayOutPlayerListHeaderFooter");
            Object packet = packetClass.newInstance();

            // Set header and footer using reflection
            try {
                Field headerField = packetClass.getDeclaredField("a");
                headerField.setAccessible(true);
                headerField.set(packet, createChatComponent(header));

                Field footerField = packetClass.getDeclaredField("b");
                footerField.setAccessible(true);
                footerField.set(packet, createChatComponent(footer));
            } catch (NoSuchFieldException e) {
                // Try alternative field names for older versions
                Field headerField = packetClass.getDeclaredField("header");
                headerField.setAccessible(true);
                headerField.set(packet, createChatComponent(header));

                Field footerField = packetClass.getDeclaredField("footer");
                footerField.setAccessible(true);
                footerField.set(packet, createChatComponent(footer));
            }

            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(playerConnection, packet);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to set tablist: " + e.getMessage());
        }
    }

    private Object createChatComponent(String text) throws Exception {
        Class<?> chatSerializerClass;
        try {
            chatSerializerClass = getNMSClass("IChatBaseComponent$ChatSerializer");
        } catch (ClassNotFoundException e) {
            chatSerializerClass = getNMSClass("ChatSerializer");
        }
        Method chatSerializerMethod = chatSerializerClass.getMethod("a", String.class);
        return chatSerializerMethod.invoke(null, "{\"text\":\"" + text.replace("\"", "\\\"") + "\"}");
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server."
                + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
    }
}
