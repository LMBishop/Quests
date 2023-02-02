package com.leonardobishop.quests.bukkit.util.chat;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.plugin.Quests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Chat {

    private static final ColorAdapter legacyColorAdapter;
    private static final Pattern legacyPattern;
    private static MiniMessageParser miniMessageParser;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];;
        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")
                || version.startsWith("v1_13") || version.startsWith("v1_14") || version.startsWith("v1_15")) {
            legacyColorAdapter = new CodedColorAdapter();
        } else {
            legacyColorAdapter = new HexColorAdapter();
        }
        Quests questsPlugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        try {
            Class.forName("net.kyori.adventure.Adventure", false, Bukkit.class.getClassLoader());
            miniMessageParser = new MiniMessageParser();
            questsPlugin.getQuestsLogger().debug("Modern chat is available.");
        } catch (Throwable e) {
            questsPlugin.getQuestsLogger().debug("Modern chat is not available, resorting to legacy chat.");
            miniMessageParser = null;
        }
        legacyPattern = Pattern.compile("(&|§)(?:\\d|#|[a-f]|[k-o]|r)");
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static String legacyColor(@Nullable String s) {
        return legacyColorAdapter.color(s);
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static List<String> legacyColor(@Nullable List<String> s) {
        if (s == null || s.size() == 0) return s;

        List<String> colored = new ArrayList<>();
        for (String line : s) {
            colored.add(legacyColorAdapter.color(line));
        }
        return colored;
    }

    @Contract("null -> null")
    public static String legacyStrip(@Nullable String s) {
        return legacyColorAdapter.strip(s);
    }

    public static boolean usesLegacy(String s) {
        return legacyPattern.matcher(s).find();
    }

    public static boolean isModernChatAvailable() {
        return miniMessageParser != null;
    }

    public static ChatColor matchConfigProblemToColor(ConfigProblem.ConfigProblemType configProblem) {
        return switch (configProblem) {
            case ERROR -> ChatColor.RED;
            case WARNING -> ChatColor.YELLOW;
            default -> ChatColor.WHITE;
        };
    }

    public static String matchConfigProblemToColorName(ConfigProblem.ConfigProblemType configProblem) {
        return switch (configProblem) {
            case ERROR -> "red";
            case WARNING -> "yellow";
            default -> "white";
        };
    }

    /**
     * Send a message to a given command sender. The given message will be parsed for legacy
     * colour, or minimessage formatting.
     *
     * @param who the player to send to
     * @param message the message to send
     * @param allowLegacy whether legacy colour codes should be tested and allowed
     * @param substitutions pairs of substitutions
     */
    public static void send(CommandSender who, String message, boolean allowLegacy, String... substitutions) {
        if (substitutions.length % 2 != 0) {
            throw new IllegalArgumentException("uneven substitutions passed");
        }

        if (message == null || message.isEmpty()) {
            return;
        }

        String substitutedMessage = message;
        for (int i = 0; i < substitutions.length ; i += 2) {
            substitutedMessage = substitutedMessage.replace(substitutions[i], substitutions[i+1]);
        }

        if (miniMessageParser == null || (allowLegacy && usesLegacy(message))) {
            who.sendMessage(legacyColor(substitutedMessage));
        } else {
            miniMessageParser.send(who, substitutedMessage);
        }
    }

    /**
     * Send a mini-message formatted message to a given command sender.
     *
     * @param who the player to send to
     * @param message the message to send
     * @param substitutions pairs of substitutions
     */
    public static void send(CommandSender who, String message, String... substitutions) {
        send(who, message, false, substitutions);
    }

}
