package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.common.config.ConfigProblem;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Chat {

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

    public static String strip(String s) {
        return ChatColor.stripColor(s);
    }

    public static ChatColor matchConfigProblemToColor(ConfigProblem.ConfigProblemType configProblem) {
        switch (configProblem) {
            case ERROR:
                return ChatColor.RED;
            case WARNING:
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }

}
