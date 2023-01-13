package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
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
                Messages.COMMAND_CATEGORY_OPEN_DISABLED.send(sender);
                return;
            }
            Category category = plugin.getQuestManager().getCategoryById(args[4]);
            if (category == null) {
                Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.send(sender, "{category}", args[4]);
                return;
            }
            Player player = Bukkit.getPlayer(args[3]);
            if (player != null) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer != null) {
                    if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
                        Messages.COMMAND_QUEST_ADMIN_CATEGORY_PERMISSION.send(sender, "{player}", player.getName(), "{category}", category.getId());
                    } else {
                        MenuUtils.openQuestCategory(plugin, qPlayer, category, null);
                        Messages.COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS.send(sender,
                                "{player}", player.getName(),
                                "{category}", category.getId());
                    }
                    return;
                }
            }
            Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.send(sender, "{player}", args[3]);
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
