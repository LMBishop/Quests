package com.leonardobishop.quests.bukkit.util.chat;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.config.ConfigProblem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private static final ColorAdapter legacyColorAdapter;
    private static final MiniMessageParser miniMessageParser;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];;
        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")
                || version.startsWith("v1_13") || version.startsWith("v1_14") || version.startsWith("v1_15")) {
            legacyColorAdapter = new CodedColorAdapter();
        } else {
            legacyColorAdapter = new HexColorAdapter();
        }
        miniMessageParser = new MiniMessageParser(Bukkit.getPluginManager().getPlugin("Quests"));
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static String color(@Nullable String s) {
        return legacyColorAdapter.color(s);
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static List<String> color(@Nullable List<String> s) {
        if (s == null || s.size() == 0) return s;

        List<String> colored = new ArrayList<>();
        for (String line : s) {
            colored.add(legacyColorAdapter.color(line));
        }
        return colored;
    }

    @Contract("null -> null")
    public static String strip(@Nullable String s) {
        return legacyColorAdapter.strip(s);
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

    public static String matchConfigProblemToColorName(ConfigProblem.ConfigProblemType configProblem) {
        switch (configProblem) {
            case ERROR:
                return "red";
            case WARNING:
                return "yellow";
            default:
                return "white";
        }
    }

    /**
     * Send a message to a given command sender. The given message will be parsed for legacy
     * colours and minimessage formatting.
     *
     * @param who the player to send to
     * @param message the message to send
     */
    public static void send(CommandSender who, String message) {
//        String colouredMessage = legacyColorAdapter.color(message);
        miniMessageParser.send(who, message);
    }

}
