package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminConfigCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminConfigCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        CommandUtils.showProblems(sender, plugin.getConfigProblems());
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
