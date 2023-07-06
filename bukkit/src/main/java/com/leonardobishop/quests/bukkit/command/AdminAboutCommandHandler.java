package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminAboutCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminAboutCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "Quests " + ChatColor.BOLD + "v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Source code: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/");
        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Report an issue: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/issues");
        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Documentation: " + ChatColor.GRAY + "https://quests.leonardobishop.com/");
        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Licensed under the GPLv3");
        sender.sendMessage(ChatColor.GRAY + "Many contributors have written source code and task types for Quests," +
                " please see the GitHub link for an up-to-date list of contributors.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
