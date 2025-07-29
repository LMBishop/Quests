package com.leonardobishop.quests.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DispatchUtils {

    private static final String PLAYER_PREFIX = "player: ";

    public static void dispatchCommand(CommandSender sender, String command) {
        if (command.startsWith(PLAYER_PREFIX)) {
            command = command.substring(PLAYER_PREFIX.length());
        } else {
            sender = Bukkit.getConsoleSender();
        }
        Bukkit.dispatchCommand(sender, command);
    }
}
