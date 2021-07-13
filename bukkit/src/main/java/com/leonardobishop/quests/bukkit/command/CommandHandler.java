package com.leonardobishop.quests.bukkit.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public interface CommandHandler {

    void handle(CommandSender sender, String[] args);

    @Nullable String getPermission();

}
