package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminWikiCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminWikiCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "Link to Quests documentation: " + ChatColor.GRAY + "https://quests.leonardobishop.com/");
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
