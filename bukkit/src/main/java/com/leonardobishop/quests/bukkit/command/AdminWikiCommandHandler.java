package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class AdminWikiCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminWikiCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "Link to Quests wiki: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/wiki");
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
