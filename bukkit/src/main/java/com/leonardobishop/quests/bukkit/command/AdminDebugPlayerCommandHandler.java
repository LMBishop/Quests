package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.player.questprogressfile.filters.QuestProgressFilter;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

public class AdminDebugPlayerCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminDebugPlayerCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        // /q a debug player <player>
        //    0 1     2      3
        Player target = args.length > 3
                ? Bukkit.getPlayer(args[3])
                : (sender instanceof Player ? (Player) sender : null);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(target.getUniqueId());
        if (qPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player data file is not loaded.");
            return;
        }

        sender.sendMessage("=== Quests debug data for " + target.getUniqueId());

        QuestProgressFile qpf = qPlayer.getQuestProgressFile();
        Iterator<Quest> questIter = qPlayer.getEffectiveStartedQuests().iterator();

        if (questIter.hasNext()) {
            sender.sendMessage("List of " + target.getName() + " started quests:");

            StringBuilder sb = new StringBuilder("[");

            while (questIter.hasNext()) {
                Quest quest = questIter.next();
                // [QUEST_ID: TASK_1{PROGRESS}, TASK_2{PROGRESS}], ...

                sb.append("[");
                sb.append(quest.getId());
                sb.append(": ");

                QuestProgress qp = qpf.getQuestProgressOrNull(quest);

                if (qp != null) {
                    Iterator<Task> taskIter = quest.getTasks().iterator();

                    while (taskIter.hasNext()) {
                        Task task = taskIter.next();
                        TaskProgress tp = qp.getTaskProgressOrNull(task.getId());

                        String progressString;
                        if (tp != null) {
                            Object progress = tp.getProgress();
                            if (progress instanceof Float || progress instanceof Double || progress instanceof BigDecimal) {
                                progressString = FormatUtils.floating((Number) progress);
                            } else if (progress instanceof Integer || progress instanceof Long || progress instanceof BigInteger) {
                                progressString = FormatUtils.integral((Number) progress);
                            } else if (progress != null) {
                                progressString = String.valueOf(progress);
                            } else {
                                progressString = "null";
                            }
                        } else {
                            progressString = "init";
                        }

                        sb.append(task.getId());
                        sb.append("{");
                        sb.append(progressString);
                        sb.append("}");

                        if (taskIter.hasNext()) {
                            sb.append(", ");
                        }
                    }
                } else {
                    sb.append("init");
                }

                sb.append("]");

                if (questIter.hasNext()) {
                    sb.append(", ");
                }
            }

            sender.sendMessage(sb.toString());
        } else {
            sender.sendMessage("No started quests found for " + target.getName() + "!");
        }

        questIter = qpf.getAllQuestsFromProgress(QuestProgressFilter.COMPLETED).iterator();

        if (questIter.hasNext()) {
            sender.sendMessage("List of " + target.getName() + " completed quests:");

            StringBuilder sb = new StringBuilder("[");

            while (questIter.hasNext()) {
                Quest quest = questIter.next();
                sb.append(quest.getId());

                if (questIter.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append("]");

            sender.sendMessage(sb.toString());
        } else {
            sender.sendMessage("No completed quests found for " + target.getName() + "!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            return null;
        } else {
            return List.of();
        }
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
