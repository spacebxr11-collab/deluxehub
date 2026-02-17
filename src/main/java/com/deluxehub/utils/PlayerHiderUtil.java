package com.deluxehub.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerHiderUtil {

    private Player player;
    private boolean hidden;

    public PlayerHiderUtil(Player player) {
        this.player = player;
        this.hidden = false;
    }

    public void toggle() {
        if (hidden) {
            showAll();
        } else {
            hideAll();
        }
    }

    public void hideAll() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.hidePlayer(other);
            }
        }
        hidden = true;
    }

    public void showAll() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.showPlayer(other);
            }
        }
        hidden = false;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void updateForNewPlayer(Player newPlayer) {
        if (hidden) {
            player.hidePlayer(newPlayer);
        }
    }
}
