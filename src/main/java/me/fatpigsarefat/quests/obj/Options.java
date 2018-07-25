package me.fatpigsarefat.quests.obj;

import me.fatpigsarefat.quests.Quests;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public enum Options {

    CATEGORIES_ENABLED("options.categories-enabled"),
    TRIM_GUI_SIZE("options.trim-gui-size"),
    QUESTS_START_LIMIT("options.quest-started-limit"),
    TITLES_ENABLED("options.titles-enabled"),
    GUI_HIDE_LOCKED("options.gui-hide-locked"),
    GUITITLE_QUESTS_CATEGORY("options.guinames.quests-category"),
    GUITITLE_QUESTS("options.guinames.quests-menu"),
    GUITITLE_DAILY_QUESTS("options.guinames.daily-quests"),
    GUITITLE_QUEST_CANCEL("options.guinames.quest-cancel"),
    ALLOW_QUEST_CANCEL("options.allow-quest-cancel");

    private String path;

    Options(String path) {
        this.path = path;
    }

    public int getIntValue() {
        return Quests.getInstance().getConfig().getInt(path);
    }

    public String getStringValue() {
        return Quests.getInstance().getConfig().getString(path);
    }

    public boolean getBooleanValue() {
        return Quests.getInstance().getConfig().getBoolean(path);
    }

    public List<String> getStringListValue() {
        return Quests.getInstance().getConfig().getStringList(path);
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
