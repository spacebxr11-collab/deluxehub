package com.deluxehub.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.deluxehub.DeluxeHub;
import com.deluxehub.utils.SpawnUtil;

public class WorldProtectionListener implements Listener {

    private DeluxeHub plugin;
    private SpawnUtil spawnUtil;

    public WorldProtectionListener(DeluxeHub plugin, SpawnUtil spawnUtil) {
        this.plugin = plugin;
        this.spawnUtil = spawnUtil;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("entity-damage")) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Allow damage if player has permission
            if (player.hasPermission("deluxehub.admin")) {
                return;
            }

            // Check for fall damage
            if (event.getCause() == DamageCause.FALL &&
                    plugin.getConfigUtil().isWorldProtectionEnabled("fall-damage")) {
                event.setCancelled(true);
                return;
            }

            // Check for void damage
            if (event.getCause() == DamageCause.VOID &&
                    plugin.getConfigUtil().isWorldProtectionEnabled("void-death")) {
                event.setCancelled(true);
                spawnUtil.teleportToSpawn(player);
                return;
            }

            // Cancel all other damage
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("hunger-loss")) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!player.hasPermission("deluxehub.admin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("item-drop")) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("deluxehub.admin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("item-pickup")) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("deluxehub.admin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("mob-spawning")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("block-burning")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("fire-spread")) {
            return;
        }

        if (event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LAVA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("fire-spread")) {
            return;
        }

        if (event.getNewState().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("leaf-decay")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigUtil().isWorldProtectionEnabled("death-messages")) {
            return;
        }

        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (spawnUtil.shouldTeleportOnRespawn()) {
            event.setRespawnLocation(spawnUtil.getSpawnLocation());
        }
    }
}
