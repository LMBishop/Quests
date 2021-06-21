package com.leonardobishop.quests.bukkit.config;

import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter;
import com.leonardobishop.quests.common.config.QuestsConfig;
import com.leonardobishop.quests.common.plugin.Quests;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitQuestsConfig implements QuestsConfig {

    private final Map<String, ItemStack> cachedItemStacks = new HashMap<>();
    // this is faster than just relying on the YamlConfiguration to cache it for some reason
    private final Map<String, Boolean> cachedBooleans = new HashMap<>();
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
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public boolean getBoolean(String path) {
        return cachedBooleans.computeIfAbsent(path, s -> config.getBoolean(path));
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return cachedBooleans.computeIfAbsent(path, s -> config.getBoolean(path, def));
    }

    @Override
    public int getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        List<String> list = config.getStringList(path);
        return list.isEmpty() ? def : list;
    }

    public ItemStack getItem(String path) {
        return new ItemStack(cachedItemStacks.computeIfAbsent(path, s -> itemGetter.getItem(path, config)));
    }
}
