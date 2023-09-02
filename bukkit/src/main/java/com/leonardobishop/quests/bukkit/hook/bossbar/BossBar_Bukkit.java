package com.leonardobishop.quests.bukkit.hook.bossbar;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;

public class BossBar_Bukkit implements BossBar {

    private BukkitQuestsPlugin plugin;
    private final ConcurrentHashMap<NamespacedKey, Long> players = new ConcurrentHashMap<>();

    public BossBar_Bukkit(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        plugin.getScheduler().runTaskTimer(() -> {
            for (Entry<NamespacedKey, Long> entry : new HashMap<>(players).entrySet()) {
                if (entry.getValue() < System.currentTimeMillis()) {
                    NamespacedKey key = entry.getKey();
                    players.remove(key);
                    org.bukkit.boss.BossBar oldBar = Bukkit.getBossBar(key);
                    if(oldBar != null) { // if exist
                        oldBar.removeAll(); // remove all players on it
                        Bukkit.removeBossBar(key); // remove it
                    }
                }
            }
        }, 20, 20);
    }

    @Override
    public void sendBossBar(Player p, String questKey, String title, int time) {
        NamespacedKey spaceKey = getKeyFor(p, questKey);
        org.bukkit.boss.BossBar bar = Bukkit.getBossBar(spaceKey);
        if (bar == null) {// if none exist
            bar = Bukkit.createBossBar(spaceKey, title, BarColor.BLUE, BarStyle.SOLID);
        } else {
            bar.setTitle(title);
        }
        players.put(spaceKey, System.currentTimeMillis() + time * 1000);
        bar.addPlayer(p); // be sure it see it
    }

    @Override
    public void sendBossBar(Player p, String questKey, String title, int percent, int time) {
        NamespacedKey spaceKey = getKeyFor(p, questKey);
        org.bukkit.boss.BossBar bar = Bukkit.getBossBar(spaceKey);
        if (bar == null) {// if none exist
            bar = Bukkit.createBossBar(spaceKey, title, BarColor.BLUE, BarStyle.SOLID);
        } else {
            bar.setTitle(title);
        }
        players.put(spaceKey, System.currentTimeMillis() + time * 1000);
        double progress = ((double) percent) / 100;
        bar.setProgress(progress < 0 ? 0 : (progress > 1 ? 1 : progress));
        bar.addPlayer(p); // be sure it see it
    }

    private NamespacedKey getKeyFor(Player p, String questKey) {
        return new NamespacedKey(plugin, "bossbar_" + p.getName().toLowerCase() + "_" + questKey.toLowerCase().replace(" ", ""));
    }
}
