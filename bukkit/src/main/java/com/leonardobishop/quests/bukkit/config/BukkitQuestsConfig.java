package com.leonardobishop.quests.bukkit.config;

import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter;
import com.leonardobishop.quests.common.config.QuestsConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitQuestsConfig implements QuestsConfig {

    private final Map<String, ItemStack> cachedItemStacks = new ConcurrentHashMap<>();
    // this is faster than just relying on the YamlConfiguration to cache it for some reason
    private final Map<String, Boolean> cachedBooleans = new ConcurrentHashMap<>();
    private final File file;
    private YamlConfiguration config;
    private ItemGetter itemGetter;

    public BukkitQuestsConfig(File file) {
        this.file = file;
    }

    public void setItemGetter(ItemGetter itemGetter) {
        this.itemGetter = itemGetter;
    }

    @Override
    public boolean loadConfig() {
        this.cachedBooleans.clear();
        this.cachedItemStacks.clear();
        this.config = new YamlConfiguration();
        try {
            config.load(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        String value = config.getString(path);
        return value == null ? path : value;
    }

    @Override
    public String getString(@NotNull String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return cachedBooleans.computeIfAbsent(path, s -> config.getBoolean(path));
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return cachedBooleans.computeIfAbsent(path, s -> config.getBoolean(path, def));
    }

    @Override
    public int getInt(@NotNull String path) {
        return config.getInt(path);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return config.getInt(path, def);
    }

    @Override
    public List<String> getStringList(@NotNull String path) {
        return config.getStringList(path);
    }

    @Override
    public List<String> getStringList(@NotNull String path, List<String> def) {
        List<String> list = config.getStringList(path);
        return list.isEmpty() ? def : list;
    }

    public ItemStack getItem(@NotNull String path) {
        return new ItemStack(cachedItemStacks.computeIfAbsent(path, s -> itemGetter.getItem(path, config)));
    }

    public int getQuestLimit(Player player) {
        if (config.contains("options.quest-started-limit")) return config.getInt("options.quest-started-limit");
        int limit = getQuestLimit("default");
        if (player != null) {
            for (String rank : config.getConfigurationSection("options.quest-limit").getKeys(false)) {
                int newLimit = getQuestLimit(rank);
                if (player.hasPermission("quests.limit." + rank) && (limit < newLimit))
                    limit = newLimit;
            }
        }
        return limit;
    }

    public int getQuestLimit(@NotNull String rank) {
        return config.getInt("options.quest-limit." + rank, config.getInt("options.quest-limit.default", 2));
    }
}
