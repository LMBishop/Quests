package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminOpenguiCategoryCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminOpenguiCategoryCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 4) {
            if (!plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                return;
            }
            Category category = plugin.getQuestManager().getCategoryById(args[4]);
            if (category == null) {
                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[4]));
                return;
            }
            Player player = Bukkit.getPlayer(args[3]);
            if (player != null) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer != null) {
                    if (plugin.getMenuController().openQuestCategory(qPlayer, category, null, false) == 0) {
                        sender.sendMessage(Messages.COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS.getMessage().replace("{player}", player.getName())
                                .replace("{category}", category.getId()));
                    } else {
                        sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_CATEGORY_PERMISSION.getMessage().replace("{player}", player.getName())
                                .replace("{category}", category.getId()));
                    }
                    return;
                }
            }
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
            return;
        }

        sender.sendMessage(ChatColor.RED + "/quests a/admin opengui c/category <player> <category>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        } else if (args.length == 5) {
            return TabHelper.tabCompleteCategory(args[4]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
