package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
                            if (Chat.isModernChatAvailable()) {
                                String color = Chat.matchConfigProblemToColorName(problem.getType());
                                String extendedDescription = problem.getExtendedDescription() != null ? String.format("<%s>%s</%s><br><gray>Problem location: </gray><white>%s</white><br><br><grey>%s</grey>",
                                        color,
                                        problem.getDescription(),
                                        color,
                                        problem.getLocation(),
                                        problem.getExtendedDescription()
                                ) : "<dark_grey>This error has no extended description</dark_grey>";
                                extendedDescription = extendedDescription.replace("'", "\\'");
                                String problemDescription = String.format("<%s>%s</%s><br>%s",
                                        color,
                                        problem.getType().getTitle(),
                                        color,
                                        problem.getType().getDescription()
                                );
                                problemDescription = problemDescription.replace("'", "\\'");

                                String message = String.format(
                                        "<dark_gray> | - </dark_gray><hover:show_text:'%s'><%s>%s</%s></hover><dark_gray>:</dark_gray> <hover:show_text:'%s'><gray>%s</gray></hover><dark_gray> :%s</dark_gray>",
                                        problemDescription,
                                        color,
                                        problem.getType().getShortened(),
                                        color,
                                        extendedDescription,
                                        problem.getDescription(),
                                        problem.getLocation()
                                );
                                Chat.send(sender, message);
                            } else {
                                sender.sendMessage(ChatColor.DARK_GRAY + " | - " + Chat.matchConfigProblemToColor(problem.getType())
                                        + problem.getType().getShortened() + ChatColor.DARK_GRAY + ": "
                                        + ChatColor.GRAY + problem.getDescription() + ChatColor.DARK_GRAY + " :" + problem.getLocation());
                            }
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
            if (Chat.isModernChatAvailable()) {
                sender.sendMessage(ChatColor.DARK_GRAY + "Mouse-over for more information.");
            }
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
            try {
                qPlayer = plugin.getPlayerManager().loadPlayer(uuid).get();
            } catch (InterruptedException | ExecutionException ignored) {}
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

        if (plugin.getPlayerManager().getPlayer(uuid) == null) {
            Messages.COMMAND_QUEST_ADMIN_LOADDATA.send(sender, "{player}", username);
            plugin.getPlayerManager().loadPlayer(uuid).thenAccept((qPlayer -> {
                if (qPlayer == null) {
                    Messages.COMMAND_QUEST_ADMIN_NODATA.send(sender, "{player}", username);
                    return;
                }

                plugin.getScheduler().doSync(() -> callback.accept(qPlayer));
            }));
        }
    }

    public static void doSafeSave(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer qPlayer) {
        final Player playerBeforeSave = Bukkit.getPlayer(qPlayer.getPlayerUUID());

        if (playerBeforeSave != null) {
            return;
        }

        plugin.getPlayerManager()
                .savePlayer(qPlayer.getPlayerData())
                .thenAccept(unused -> plugin.getScheduler().doSync(() -> {
                    final Player playerAfterSave = Bukkit.getPlayer(qPlayer.getPlayerUUID());

                    if (playerAfterSave == null) {
                        plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
                    }
                }));
    }
}
