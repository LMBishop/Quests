package com.leonardobishop.quests.obj;

import com.leonardobishop.quests.Quests;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Options {
    CATEGORIES_ENABLED("options.categories-enabled"),
    TRIM_GUI_SIZE("options.trim-gui-size"),
    QUESTS_START_LIMIT("options.quest-started-limit"),
    TITLES_ENABLED("options.titles-enabled"),
    GUI_HIDE_LOCKED("options.gui-hide-locked"),
    GUI_HIDE_QUESTS_NOPERMISSION("options.gui-hide-quests-nopermission"),
    GUI_HIDE_CATEGORIES_NOPERMISSION("options.gui-hide-categories-nopermission"),
    GUI_USE_PLACEHOLDERAPI("options.gui-use-placeholderapi"),
    GUITITLE_QUESTS_CATEGORY("options.guinames.quests-category"),
    GUITITLE_QUESTS("options.guinames.quests-menu"),
    GUITITLE_DAILY_QUESTS("options.guinames.daily-quests"),
    GUITITLE_QUEST_CANCEL("options.guinames.quest-cancel"),
    ALLOW_QUEST_CANCEL("options.allow-quest-cancel"),
    ALLOW_QUEST_TRACK("options.allow-quest-track"),
    QUEST_AUTOSAVE_ASYNC("options.performance-tweaking.quests-autosave-async"),
    SOFT_CLEAN_QUESTSPROGRESSFILE_ON_JOIN("options.soft-clean-questsprogressfile-on-join"),
    PUSH_SOFT_CLEAN_TO_DISK("options.tab-completion.push-soft-clean-to-disk"),
    TAB_COMPLETE_ENABLED("options.tab-completion.enabled"),
    ERROR_CHECKING_OVERRIDE("options.error-checking.override-errors"),
    QUEST_AUTOSTART("options.quest-autostart"),
    QUEST_AUTOTRACK("options.quest-autotrack"),
    GLOBAL_TASK_CONFIGURATION_OVERRIDE("options.global-task-configuration-override"),
    GLOBAL_QUEST_DISPLAY_CONFIGURATION_OVERRIDE("options.global-quest-display-configuration-override"),
    GLOBAL_QUEST_DISPLAY_LORE_APPEND_NORMAL("global-quest-display.lore.append-normal"),
    GLOBAL_QUEST_DISPLAY_LORE_APPEND_NOT_STARTED("global-quest-display.lore.append-not-started"),
    GLOBAL_QUEST_DISPLAY_LORE_APPEND_STARTED("global-quest-display.lore.append-started"),
    GLOBAL_QUEST_DISPLAY_LORE_APPEND_TRACKED("global-quest-display.lore.append-tracked");

    private static final Map<String, Boolean> cachedBooleans = new HashMap<>();

    private final String path;

    Options(String path) {
        this.path = path;
    }

    public int getIntValue() {
        return Quests.get().getConfig().getInt(path);
    }

    public int getIntValue(int def) {
        return Quests.get().getConfig().getInt(path, def);
    }

    public String getStringValue() {
        return Quests.get().getConfig().getString(path);
    }

    public String getStringValue(String def) {
        return Quests.get().getConfig().getString(path, def);
    }

    public boolean getBooleanValue() {
        Boolean val = cachedBooleans.get(path);
        if (val != null) {
            return val;
        } else {
            cachedBooleans.put(path, Quests.get().getConfig().getBoolean(path));
            return getBooleanValue();
        }
    }

    public boolean getBooleanValue(boolean def) {
        return Quests.get().getConfig().getBoolean(path, def);
    }

    public List<String> getStringListValue() {
        return Quests.get().getConfig().getStringList(path);
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(List<String> s) {
        if (s == null || s.size() == 0) return s;

        List<String> colored = new ArrayList<>();
        for (String line : s) {
            colored.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return colored;
    }

    public static void invalidateCaches() {
        cachedBooleans.clear();
    }
}
