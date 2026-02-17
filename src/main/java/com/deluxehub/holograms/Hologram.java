package com.deluxehub.holograms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.PlaceholderUtil;
import com.deluxehub.utils.ReflectionUtil;

public class Hologram {

    private Location location;
    private List<String> lines;
    private List<Object> armorStands; // NMS EntityArmorStand objects
    private List<Integer> entityIds;
    private PlaceholderUtil placeholderUtil;

    public Hologram(DeluxeHub plugin, Location location, List<String> lines) {
        this.location = location;
        this.lines = new ArrayList<>(lines);
        this.armorStands = new ArrayList<>();
        this.entityIds = new ArrayList<>();
        this.placeholderUtil = new PlaceholderUtil(plugin);
        createHologramEntities();
    }

    // Initialize NMS entities (server-side objects, but not added to world)
    private void createHologramEntities() {
        armorStands.clear();
        entityIds.clear();

        try {
            Class<?> worldClass = ReflectionUtil.getNMSClass("World");
            Class<?> armorStandClass = ReflectionUtil.getNMSClass("EntityArmorStand");
            Object nmsWorld = ReflectionUtil.getHandle(location.getWorld());

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Location lineLoc = location.clone().add(0, (lines.size() - i) * 0.25, 0);

                Object armorStand = armorStandClass.getConstructor(worldClass).newInstance(nmsWorld);

                // Set location
                armorStandClass
                        .getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                        .invoke(armorStand, lineLoc.getX(), lineLoc.getY(), lineLoc.getZ(), 0f, 0f);

                // Configure
                armorStandClass.getMethod("setCustomName", String.class).invoke(armorStand, line);
                armorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(armorStand, true);
                armorStandClass.getMethod("setInvisible", boolean.class).invoke(armorStand, true);
                armorStandClass.getMethod("setSmall", boolean.class).invoke(armorStand, true); // For 1.8.8
                                                                                               // functionality

                // Store ID
                int id = (int) armorStandClass.getMethod("getId").invoke(armorStand);
                entityIds.add(id);
                armorStands.add(armorStand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawn(Player player) {
        try {
            Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntityLiving");
            Class<?> entityLivingClass = ReflectionUtil.getNMSClass("EntityLiving");

            for (Object armorStand : armorStands) {
                // Update placeholder for player
                String line = (String) ReflectionUtil.getNMSClass("EntityArmorStand").getMethod("getCustomName")
                        .invoke(armorStand);
                line = placeholderUtil.setPlaceholders(player, line);
                ReflectionUtil.getNMSClass("EntityArmorStand").getMethod("setCustomName", String.class)
                        .invoke(armorStand, line);

                Object packet = packetClass.getConstructor(entityLivingClass).newInstance(armorStand);
                ReflectionUtil.sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy(Player player) {
        try {
            Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy");
            int[] ids = new int[entityIds.size()];
            for (int i = 0; i < entityIds.size(); i++) {
                ids[i] = entityIds.get(i);
            }

            Object packet = packetClass.getConstructor(int[].class).newInstance(ids);
            ReflectionUtil.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        // Destroy for all online players
        for (Player player : location.getWorld().getPlayers()) {
            destroy(player);
        }
    }

    public void update(Player player) {
        destroy(player);
        spawn(player);
    }

    public void update() {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distance(location) < 50) { // Optimize distance
                update(player);
            }
        }
    }

    public Location getLocation() {
        return location;
    }
}
