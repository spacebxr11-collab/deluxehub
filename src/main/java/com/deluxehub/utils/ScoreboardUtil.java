package com.deluxehub.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.deluxehub.DeluxeHub;

public class ScoreboardUtil {
    
    private DeluxeHub plugin;
    private PlaceholderUtil placeholderUtil;
    
    public ScoreboardUtil(DeluxeHub plugin) {
        this.plugin = plugin;
        this.placeholderUtil = new PlaceholderUtil(plugin);
    }
    
    public void setScoreboard(Player player) {
        if (!plugin.getConfigUtil().isFeatureEnabled("scoreboard")) {
            return;
        }
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("deluxehub", "dummy");
        
        String title = placeholderUtil.setPlaceholders(player, 
            plugin.getConfig().getString("scoreboard.title", "&6&lDELUXEHUB"));
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        
        // Add lines in reverse order (scoreboard displays from bottom to top)
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = placeholderUtil.setPlaceholders(player, lines.get(i));
            if (line.length() > 40) line = line.substring(0, 40); // 1.8.9 limitation
            
            Score score = objective.getScore(line);
            score.setScore(i);
        }
        
        player.setScoreboard(board);
    }
    
    public void removeScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            player.setScoreboard(manager.getMainScoreboard());
        }
    }
    
    public void updateScoreboard(Player player) {
        setScoreboard(player);
    }
}
