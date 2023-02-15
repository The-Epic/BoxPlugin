package me.twostinkysocks.boxplugin.manager;

import me.twostinkysocks.boxplugin.BoxPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private ArrayList<Player> queuedScoreboardUpdates;

    public ScoreboardManager() {
        queuedScoreboardUpdates = new ArrayList<Player>();
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, () -> {
            for(Player p : BoxPlugin.instance.getScoreboardManager().getQueuedUpdates()) {
                updatePlayerScoreboard(p);
            }
            BoxPlugin.instance.getScoreboardManager().clearQueuedUpdates();
        }, 20L, 20L);
    }

    public void queueUpdate(Player p) {
        if(!queuedScoreboardUpdates.contains(p)) {
            queuedScoreboardUpdates.add(p);
        }
    }

    public ArrayList<Player> getQueuedUpdates() {
        return queuedScoreboardUpdates;
    }

    public void clearQueuedUpdates() {
        queuedScoreboardUpdates = new ArrayList<>();
    }


    public void updatePlayerScoreboard(Player p) {

        List<String> list = BoxPlugin.instance.getConfig().getList("scoreboard").stream().map(s -> ChatColor.translateAlternateColorCodes('&', (String) s)).collect(Collectors.toList());

        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy", list.remove(0));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        List<Score> scores = new ArrayList<Score>();

        for(int i = 0; i < list.size(); i++) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes(
                    '&',
                    list.get(i).replaceAll("%level%", "" + BoxPlugin.instance.getXpManager().getLevel(p)).replaceAll("%needed-xp%","" + BoxPlugin.instance.getXpManager().getNeededXp(p)).replaceAll("%streak%", "" + BoxPlugin.instance.getPvpManager().getStreak(p)).replaceAll("%bounty%", "" + BoxPlugin.instance.getPvpManager().getBounty(p))
            ));
            scores.add(score);
            score.setScore(list.size()-i-1);
        }
        p.setScoreboard(scoreboard);
    }
}
