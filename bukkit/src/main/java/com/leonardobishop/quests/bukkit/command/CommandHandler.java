package com.leonardobishop.quests.bukkit.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CommandHandler {

    void handle(CommandSender sender, String[] args);

    List<String> tabComplete(CommandSender sender, String[] args);

    @Nullable String getPermission();

}
