package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CategoryCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public CategoryCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
            Messages.COMMAND_CATEGORY_OPEN_DISABLED.send(sender);
            return;
        }
        Player player = (Player) sender;
        if (args.length >= 2) {
            Category category = plugin.getQuestManager().getCategoryById(args[1]);
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                Messages.COMMAND_DATA_NOT_LOADED.send(player);
                return;
            }
            if (category == null) {
                Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.send(sender, "{category}", args[1]);
            } else if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
                Messages.COMMAND_QUEST_ADMIN_CATEGORY_PERMISSION.send(sender, "{player}", player.getName(), "{category}", category.getId());
            } else {
                MenuUtils.openQuestCategory(plugin, qPlayer, category, null);
                Messages.COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS.send(sender,
                        "{player}", player.getName(),
                        "{category}", category.getId());
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + "/quests c/category <categoryid>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabHelper.tabCompleteCategory(args[1]);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.command.category";
    }

}
