package com.leonardobishop.quests.bukkit.hook.bossbar;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class BossBar_Bukkit implements QuestsBossBar {

    private final BukkitQuestsPlugin plugin;
    private final RemovalListener<String, BossBar> removalListener;

    // use cache because of its concurrency and automatic player on quit removal
    private final Cache<Player, Cache<String, BossBar>> playerQuestBarCache = CacheBuilder.newBuilder().weakKeys().build();

    public BossBar_Bukkit(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.removalListener = removal -> plugin.getScheduler().runTask(() -> removal.getValue().removeAll());

        //noinspection CodeBlock2Expr (for readability)
        plugin.getScheduler().runTaskTimerAsynchronously(() -> {
            playerQuestBarCache.asMap()
                    .values()
                    .forEach(Cache::cleanUp);
        }, 0L, 2L);
    }

    @Override
    public void sendBossBar(Player player, String questId, String title, int time) {
        sendBossBar(player, questId, title, time, 1.0f);
    }

    @Override
    public void sendBossBar(Player player, String questId, String title, int time, float progress) {
        plugin.getScheduler().runTaskAsynchronously(() -> {
            Cache<String, BossBar> questBarCache = playerQuestBarCache.asMap()
                    .computeIfAbsent(player, k -> {
                        //noinspection CodeBlock2Expr (for readability)
                        return CacheBuilder.newBuilder()
                                .expireAfterAccess(time, TimeUnit.SECONDS)
                                .removalListener(removalListener)
                                .build();
                    });

            BossBar bar = questBarCache.asMap()
                    .computeIfAbsent(questId, k -> {
                        //noinspection CodeBlock2Expr (for readability)
                        return Bukkit.createBossBar(null, BarColor.BLUE, BarStyle.SOLID);
                    });

            bar.setTitle(title);
            bar.setProgress(progress);

            plugin.getScheduler().runTask(() -> bar.addPlayer(player));
        });
    }
}
