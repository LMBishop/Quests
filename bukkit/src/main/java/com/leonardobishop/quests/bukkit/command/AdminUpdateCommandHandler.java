package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminUpdateCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminUpdateCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "Checking for updates...");
        plugin.getScheduler().doAsync(() -> {
            plugin.getUpdater().check();
            if (plugin.getUpdater().isUpdateReady()) {
                Messages.QUEST_UPDATER.send(sender,
                        "{newver}", plugin.getUpdater().getReturnedVersion(),
                        "{oldver}", plugin.getUpdater().getInstalledVersion(),
                        "{link}", plugin.getUpdater().getUpdateLink());
            } else {
                sender.sendMessage(ChatColor.GRAY + "No updates were found.");
            }
        });
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
