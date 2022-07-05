package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.questcompleter.BukkitQuestCompleter;
import com.leonardobishop.quests.bukkit.util.LogHistory;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class AdminDebugQuestCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;


    public AdminDebugQuestCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Your data file is not loaded.");
                return;
            }

            String questId = args[3];
            Quest quest = plugin.getQuestManager().getQuestById(questId);
            if (quest == null) {
                sender.sendMessage(ChatColor.RED + "Quest " + questId + " does not exist.");
                return;
            }

            QPlayerPreferences preferences = qPlayer.getPlayerPreferences();
            QPlayerPreferences.DebugType currentDebugType = preferences.getDebug(questId);
            if (currentDebugType == null) {
                String debugType = args[4];
                QPlayerPreferences.DebugType debugTypeEnum;

                try {
                    debugTypeEnum = QPlayerPreferences.DebugType.valueOf(debugType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid debug type.");
                    return;
                }

                preferences.setDebug(questId, debugTypeEnum);
                sender.sendMessage(ChatColor.GREEN + "Debugging enabled for quest '" + questId + "'.");
                sender.sendMessage(ChatColor.GRAY + "You will now see debug logs for quest '" + quest + "' for " +
                        (debugTypeEnum == QPlayerPreferences.DebugType.SELF ? "yourself" : "everybody on the server") +
                        ". This may generate a lot of spam.");
                sender.sendMessage(ChatColor.DARK_GRAY + "Use '/quests admin debug " + questId + "' to disable.");
            } else {
                preferences.setDebug(questId, null);
                sender.sendMessage(ChatColor.GREEN + "Debugging disabled for quest '" + questId + "'.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return TabHelper.tabCompleteQuests(args[3]);
        } else if (args.length == 5) {
            return TabHelper.matchTabComplete(args[2], Arrays.asList("self", "all"));
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
