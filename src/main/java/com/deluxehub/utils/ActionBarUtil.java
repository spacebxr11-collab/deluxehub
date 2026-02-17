package com.deluxehub.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.deluxehub.DeluxeHub;

public class ActionBarUtil {

    private DeluxeHub plugin;

    public ActionBarUtil(DeluxeHub plugin) {
        this.plugin = plugin;
    }

    public void sendActionBar(Player player, String message) {
        if (message == null || message.isEmpty())
            return;

        try {
            message = plugin.color(message);

            Object chatComponent = null;
            Class<?> chatSerializerClass = ReflectionUtil.getNMSClass("IChatBaseComponent$ChatSerializer");

            if (chatSerializerClass == null) {
                chatSerializerClass = ReflectionUtil.getNMSClass("ChatSerializer");
            }

            if (chatSerializerClass != null) {
                Method a = chatSerializerClass.getMethod("a", String.class);
                chatComponent = a.invoke(null, "{\"text\":\"" + message + "\"}");
            } else {
                plugin.getLogger().warning("Could not find ChatSerializer class!");
                return;
            }

            Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutChat");
            Class<?> componentClass = ReflectionUtil.getNMSClass("IChatBaseComponent");
            Constructor<?> packetConstructor = packetClass.getConstructor(componentClass, byte.class);

            Object packet = packetConstructor.newInstance(chatComponent, (byte) 2);

            ReflectionUtil.sendPacket(player, packet);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send action bar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastActionBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, message);
        }
    }
}
