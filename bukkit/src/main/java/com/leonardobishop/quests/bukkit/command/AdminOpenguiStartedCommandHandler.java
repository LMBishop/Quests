package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminOpenguiStartedCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminOpenguiStartedCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 3) {
            Player player = Bukkit.getPlayer(args[3]);
            if (player != null) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer != null) {
                    MenuUtils.openStartedQuests(plugin, qPlayer);
                    Messages.COMMAND_QUEST_OPENSTARTED_ADMIN_SUCCESS.send(sender,
                            "{player}", player.getName());
                    return;
                }
            }
            Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.send(sender, "{player}", args[3]);
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin opengui s/started <player>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
