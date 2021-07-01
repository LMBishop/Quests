package com.leonardobishop.quests.bukkit.util.chat;

import com.leonardobishop.quests.common.config.ConfigProblem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private static final ColorAdapter colorAdapter;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];;
        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")
                || version.startsWith("v1_13") || version.startsWith("v1_14") || version.startsWith("v1_15")) {
            colorAdapter = new CodedColorAdapter();
        } else {
            colorAdapter = new HexColorAdapter();
        }
    }

    @Contract("null -> null")
    public static String color(@Nullable String s) {
        return colorAdapter.color(s);
    }

    @Contract("null -> null")
    public static List<String> color(@Nullable List<String> s) {
        if (s == null || s.size() == 0) return s;

        List<String> colored = new ArrayList<>();
        for (String line : s) {
            colored.add(colorAdapter.color(line));
        }
        return colored;
    }

    @Contract("null -> null")
    public static String strip(@Nullable String s) {
        return colorAdapter.strip(s);
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
