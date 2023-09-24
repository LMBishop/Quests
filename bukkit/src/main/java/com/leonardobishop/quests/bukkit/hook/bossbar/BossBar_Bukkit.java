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
import java.util.logging.Level;

public class BossBar_Bukkit implements QuestsBossBar {

    private final BukkitQuestsPlugin plugin;
    private final RemovalListener<String, BossBar> removalListener;
    private BarColor barColor = BarColor.BLUE;
    private BarStyle barStyle = BarStyle.SOLID;

    // use cache because of its concurrency and automatic player on quit removal
    private final Cache<Player, Cache<String, BossBar>> playerQuestBarCache = CacheBuilder.newBuilder().weakKeys().build();

    public BossBar_Bukkit(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.removalListener = removal -> plugin.getScheduler().runTask(() -> removal.getValue().removeAll());

        try {
            String barColorString = plugin.getQuestsConfig().getString("options.bossbar.color", this.barColor.name());
            this.barColor = BarColor.valueOf(barColorString);

            String barStyleString = plugin.getQuestsConfig().getString("options.bossbar.style", this.barStyle.name());
            this.barStyle = BarStyle.valueOf(barStyleString);
        } catch (IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not set color or style for the initialized boss bar implementation, using default instead!", e);
        }

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
                        return Bukkit.createBossBar(null, this.barColor, this.barStyle);
                    });

            bar.setTitle(title);
            bar.setProgress(progress);

            plugin.getScheduler().runTask(() -> bar.addPlayer(player));
        });
    }
}
