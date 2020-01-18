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
    GUITITLE_QUESTS_CATEGORY("options.guinames.quests-category"),
    GUITITLE_QUESTS("options.guinames.quests-menu"),
    GUITITLE_DAILY_QUESTS("options.guinames.daily-quests"),
    GUITITLE_QUEST_CANCEL("options.guinames.quest-cancel"),
    ALLOW_QUEST_CANCEL("options.allow-quest-cancel"),
    QUEST_AUTOSTART("options.quest-autostart");

    private final String path;

    Options(String path) {
        this.path = path;
    }

    public int getIntValue() {
        return Quests.get().getConfig().getInt(path);
    }

    public String getStringValue() {
        return Quests.get().getConfig().getString(path);
    }

    public boolean getBooleanValue() {
        return Quests.get().getConfig().getBoolean(path);
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
