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
import java.util.Objects;
import java.util.regex.Pattern;

public final class Chat {

    private static final ColorAdapter legacyColorAdapter;
    private static final Pattern legacyPattern;
    private static MiniMessageParser miniMessageParser;

    static {
        String[] versionPartStrings = Bukkit.getBukkitVersion().split("-", 2)[0].split("\\.", 3);
        int versionPartCount = versionPartStrings.length;

        int[] versionParts = new int[versionPartCount];
        for (int i = 0; i < versionPartCount; i++) {
            try {
                versionParts[i] = Integer.parseInt(versionPartStrings[i]);
            } catch (NumberFormatException ignored) {
                versionParts[i] = 0;
            }
        }

        if (versionParts[0] <= 1 && versionParts[1] <= 15) {
            legacyColorAdapter = new CodedColorAdapter();
        } else {
            legacyColorAdapter = new HexColorAdapter();
        }

        legacyPattern = Pattern.compile("(?i)[&§][0-9A-FK-ORX#]");

        Quests plugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        Objects.requireNonNull(plugin);

        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage", false, Bukkit.class.getClassLoader());

            miniMessageParser = new MiniMessageParser();
            plugin.getQuestsLogger().debug("Modern chat is available.");
        } catch (ClassNotFoundException ignored) {
            plugin.getQuestsLogger().debug("Modern chat is not available, using legacy chat instead.");
        }
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static String legacyColor(@Nullable String s) {
        return legacyColorAdapter.color(s);
    }

    @Contract("null -> null")
    @Deprecated // use send instead
    public static List<String> legacyColor(@Nullable List<String> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        List<String> coloredList = new ArrayList<>();
        for (String s : list) {
            String colored = legacyColorAdapter.color(s);
            coloredList.add(colored);
        }

        return coloredList;
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

    @SuppressWarnings("deprecation")
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
        for (int i = 0; i < substitutions.length; i += 2) {
            substitutedMessage = substitutedMessage.replace(substitutions[i], substitutions[i + 1]);
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
