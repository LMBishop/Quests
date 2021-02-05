package com.leonardobishop.quests.obj;

import com.leonardobishop.quests.Quests;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

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
    SOFT_CLEAN_QUESTSPROGRESSFILE_ON_JOIN("options.soft-clean-questsprogressfile-on-join"),
    PUSH_SOFT_CLEAN_TO_DISK("options.tab-completion.push-soft-clean-to-disk"),
    TAB_COMPLETE_ENABLED("options.tab-completion.enabled"),
    ERROR_CHECKING_OVERRIDE("options.error-checking.override-errors"),
    QUEST_AUTOSTART("options.quest-autostart");

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
        return Quests.get().getConfig().getBoolean(path);
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
        List<String> colored = new ArrayList<>();
        for (String line : s) {
            colored.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return colored;
    }
}
