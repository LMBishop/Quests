package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.Consumer;

public class CommandUtils {

    public static void showProblems(CommandSender sender, Map<String, List<ConfigProblem>> problems) {
        if (!problems.isEmpty()) {
//            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            sender.sendMessage(ChatColor.GRAY + "Detected problems and potential issues:");
            Set<ConfigProblem.ConfigProblemType> problemTypes = new HashSet<>();
            int count = 0;
            for (Map.Entry<String, List<ConfigProblem>> entry : problems.entrySet()) {
                HashMap<ConfigProblem.ConfigProblemType, List<ConfigProblem>> sortedProblems = new HashMap<>();
                for (ConfigProblem problem : entry.getValue()) {
                    if (sortedProblems.containsKey(problem.getType())) {
                        sortedProblems.get(problem.getType()).add(problem);
                    } else {
                        List<ConfigProblem> specificProblems = new ArrayList<>();
                        specificProblems.add(problem);
                        sortedProblems.put(problem.getType(), specificProblems);
                    }
                    problemTypes.add(problem.getType());
                }
                ConfigProblem.ConfigProblemType highest = null;
                for (ConfigProblem.ConfigProblemType type : ConfigProblem.ConfigProblemType.values()) {
                    if (sortedProblems.containsKey(type)) {
                        highest = type;
                        break;
                    }
                }
                ChatColor highestColor = ChatColor.WHITE;
                if (highest != null) {
                    highestColor = Chat.matchConfigProblemToColor(highest);
                }
                sender.sendMessage(highestColor + entry.getKey() + ChatColor.DARK_GRAY + " ----");
                for (ConfigProblem.ConfigProblemType type : ConfigProblem.ConfigProblemType.values()) {
                    if (sortedProblems.containsKey(type)) {
                        for (ConfigProblem problem : sortedProblems.get(type)) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " | - " + Chat.matchConfigProblemToColor(problem.getType())
                                    + problem.getType().getShortened() + ChatColor.DARK_GRAY + ": "
                                    + ChatColor.GRAY + problem.getDescription() + ChatColor.DARK_GRAY + " :" + problem.getLocation());
                            count++;
                        }
                    }
                }
            }
//                            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            List<String> legend = new ArrayList<>();
            for (ConfigProblem.ConfigProblemType type : ConfigProblem.ConfigProblemType.values()) {
                if (problemTypes.contains(type)) {
                    legend.add(Chat.matchConfigProblemToColor(type) + type.getShortened() + ChatColor.DARK_GRAY + " = " + Chat.matchConfigProblemToColor(type) + type.getTitle());
                }
            }
            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");

            sender.sendMessage(ChatColor.GRAY.toString() + count + " problem(s) | " + String.join(ChatColor.DARK_GRAY + ", ", legend));
        } else {
            sender.sendMessage(ChatColor.GRAY + "Quests did not detect any problems with your configuration.");
        }
    }

    public static QPlayer getOtherPlayerSync(CommandSender sender, String name, BukkitQuestsPlugin plugin) {
        OfflinePlayer ofp = Bukkit.getOfflinePlayer(name);
        UUID uuid;
        String username;
        if (ofp.getName() != null) {
            uuid = ofp.getUniqueId();
            username = ofp.getName();
        } else {
            Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.send(sender, "{player}", name);
            return null;
        }
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
        if (qPlayer == null) {
            Messages.COMMAND_QUEST_ADMIN_LOADDATA.send(sender, "{player}", username);
            plugin.getPlayerManager().loadPlayer(uuid);
            qPlayer = plugin.getPlayerManager().getPlayer(uuid);
        }
        if (qPlayer == null) {
            Messages.COMMAND_QUEST_ADMIN_NODATA.send(sender, "{player}", username);
            return null;
        }
        return qPlayer;
    }

    public static void useOtherPlayer(CommandSender sender, String name, BukkitQuestsPlugin plugin, Consumer<QPlayer> callback) {
        OfflinePlayer ofp = Bukkit.getOfflinePlayer(name);
        UUID uuid;
        String username;
        if (ofp.getName() != null) {
            uuid = ofp.getUniqueId();
            username = ofp.getName();
        } else {
            Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.send(sender, "{player}", name);
            return;
        }

        {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
            if (qPlayer != null) {
                callback.accept(qPlayer);
                return;
            }
        }

        plugin.getScheduler().doAsync(() -> {
            if (plugin.getPlayerManager().getPlayer(uuid) == null) {
                Messages.COMMAND_QUEST_ADMIN_LOADDATA.send(sender, "{player}", username);
                plugin.getPlayerManager().loadPlayer(uuid);
            }

            final QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);

            if (qPlayer == null) {
                Messages.COMMAND_QUEST_ADMIN_NODATA.send(sender, "{player}", username);
                return;
            }

            plugin.getScheduler().doSync(() -> callback.accept(qPlayer));
        });
    }

    public static void doSafeSave(QPlayer qPlayer, QuestProgressFile questProgressFile, BukkitQuestsPlugin plugin) {
        if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
            plugin.getScheduler().doAsync(() -> {
                plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
                plugin.getScheduler().doSync(() -> {
                    if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                        plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
                    }
                });
            });
        }
    }

}
