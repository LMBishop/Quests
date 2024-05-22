package com.leonardobishop.quests.bukkit.hook.bossbar;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BossBar_Bukkit implements QuestsBossBar {

    private final BukkitQuestsPlugin plugin;
    private final RemovalListener<String, BossBar> removalListener;
    private final Map<Float, BarColor> barColorMap;
    private final Map<Float, BarStyle> barStyleMap;

    // use cache because of its concurrency and automatic player on quit removal
    private final Cache<Player, Cache<String, BossBar>> playerQuestBarCache = CacheBuilder.newBuilder().weakKeys().build();

    public BossBar_Bukkit(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.removalListener = removal -> plugin.getScheduler().runTask(() -> removal.getValue().removeAll());

        // Load bossbar color config
        this.barColorMap = loadConfig(BarColor.class, "color", BarColor.BLUE);

        // Load bossbar style config
        this.barStyleMap = loadConfig(BarStyle.class, "style", BarStyle.SOLID);

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

            BarColor color = getBest(barColorMap, progress);
            BarStyle style = getBest(barStyleMap, progress);

            BossBar bar = questBarCache.asMap()
                    .computeIfAbsent(questId, k -> {
                        //noinspection CodeBlock2Expr (for readability)
                        return Bukkit.createBossBar(null, color, style);
                    });

            bar.setTitle(title);
            bar.setProgress(progress);

            if (bar.getColor() != color) {
                bar.setColor(color);
            }

            if (bar.getStyle() != style) {
                bar.setStyle(style);
            }

            plugin.getScheduler().runTask(() -> bar.addPlayer(player));
        });
    }

    private <T extends Enum<T>> @NotNull Map<Float, T> loadConfig(Class<T> clazz, String type, T def) {
        Map<Float, T> map;
        try {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("options.bossbar." + type);
            if (section == null) {
                throw new IllegalStateException(type + " section is missing");
            }

            map = new HashMap<>();

            for (String progressString : section.getKeys(true)) {
                if (!section.isString(progressString)) {
                    continue;
                }

                float progress = Float.parseFloat(progressString);
                if (progress < 0.0f || progress > 1.0f) {
                    throw new IllegalArgumentException("dynamic " + type + " progress must be between 0.0 and 1.0");
                }

                String tString = section.getString(progressString);
                if (tString == null) {
                    throw new IllegalStateException(type + " for " + progressString + " is missing");
                }

                T t = Enum.valueOf(clazz, tString);
                map.put(progress, t);
            }

            if (map.isEmpty()) {
                throw new IllegalStateException(type + " section is empty");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not set " + type + " for the initialized boss bar implementation, using default instead!");
            plugin.getLogger().log(Level.SEVERE, "Update your config to latest version! (" + e.getMessage() + ")");
            plugin.getLogger().log(Level.SEVERE, "https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/resources/resources/bukkit/config.yml");
            map = Collections.singletonMap(0.0f, def);
        }
        return map;
    }

    private @NotNull <T> T getBest(Map<Float, T> map, float progress) {
        Iterator<Float> it = map.keySet().iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("map cannot be empty");
        }

        Float best = it.next();
        while (it.hasNext()) {
            Float key = it.next();
            if (key <= progress) {
                best = Math.max(best, key);
            }
        }

        return map.get(best);
    }
}
