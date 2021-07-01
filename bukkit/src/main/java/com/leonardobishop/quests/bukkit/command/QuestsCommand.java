package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class QuestsCommand implements TabExecutor {

    private final BukkitQuestsPlugin plugin;

    public QuestsCommand(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (plugin.getTaskTypeManager().areRegistrationsAccepted()) {
            sender.sendMessage(ChatColor.RED + "Quests is not ready yet.");
            return true;
        }
        if (!plugin.isValidConfiguration()
                && !(args.length >= 2
                && (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin"))
                && args[1].equalsIgnoreCase("reload"))) {
            sender.sendMessage(ChatColor.RED + "Quests cannot be used right now. Please speak to an administrator.");
            if (sender.hasPermission("quests.admin")) {
                showProblems(sender);
                sender.sendMessage(ChatColor.RED + "The main config (config.yml) must be in tact before quests can be used. " +
                        "Please use the above information to help rectify the problem.");
            }
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                return true;
            }
            plugin.getMenuController().openMainMenu(qPlayer);
            return true;
        } else if (args.length >= 1) {
            if ((args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")) && sender.hasPermission("quests.admin")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        showAdminHelp(sender, "moddata");
                        return true;
                    } else if (args[1].equalsIgnoreCase("reload")) {
                        sender.sendMessage(ChatColor.GRAY + "Please note that some options, such as storage, require a full restart for chances to take effect.");
                        plugin.reloadConfig();
                        plugin.reloadQuests();
                        if (!plugin.getConfigProblems().isEmpty()) showProblems(sender);
                        sender.sendMessage(ChatColor.GREEN + "Quests successfully reloaded.");
                        return true;
                    } else if (args[1].equalsIgnoreCase("config")) {
                        showProblems(sender);
                        return true;
                        //TODO
//                    } else if (args[1].equalsIgnoreCase("itemstack")) {
//                        if (!(sender instanceof Player)) {
//                            sender.sendMessage("You must be a player to use this command.");
//                            return true;
//                        }
//                        Player player = (Player) sender;
//                        ItemStack is = player.getItemInHand();
//                        if (is == null || is.getType() == Material.AIR) {
//                            sender.sendMessage(ChatColor.GRAY + "There is no information about this ItemStack.");
//                            return true;
//                        }
//                        sender.sendMessage(ToStringBuilder.reflectionToString(is));
//                        sender.sendMessage(ToStringBuilder.reflectionToString(is.getItemMeta()));
//                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        sender.sendMessage(ChatColor.GRAY + "Registered task types:");
                        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + taskType.getType());
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a types [type].");
                        return true;
                    } else if (args[1].equalsIgnoreCase("info")) {
                        sender.sendMessage(ChatColor.RED + "Quest controller: " + plugin.getQuestController().getName());
                        sender.sendMessage(ChatColor.GRAY + "Loaded quests:");
                        for (Quest quest : plugin.getQuestManager().getQuests().values()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + quest.getId() + ChatColor.GRAY + " [" + quest.getTasks().size() + " tasks]");
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a info [quest].");
                        return true;
                    } else if (args[1].equalsIgnoreCase("update")) {
                        sender.sendMessage(ChatColor.GRAY + "Checking for updates...");
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            plugin.getUpdater().check();
                            if (plugin.getUpdater().isUpdateReady()) {
                                String updateMessage = Messages.QUEST_UPDATER.getMessage()
                                        .replace("{newver}", plugin.getUpdater().getReturnedVersion())
                                        .replace("{oldver}", plugin.getUpdater().getInstalledVersion())
                                        .replace("{link}", plugin.getUpdater().getUpdateLink());
                                sender.sendMessage(updateMessage);
                            } else {
                                sender.sendMessage(ChatColor.GRAY + "No updates were found.");
                            }
                        });
                        return true;
                    } else if (args[1].equalsIgnoreCase("wiki")) {
                        sender.sendMessage(ChatColor.RED + "Link to Quests wiki: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/wiki");
                        return true;
                    } else if (args[1].equalsIgnoreCase("about")) {
                        sender.sendMessage(ChatColor.RED + "Quests " + ChatColor.BOLD + "v" + plugin.getDescription().getVersion());
                        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Source code: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/");
                        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Report an issue: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/issues");
                        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Wiki: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/wiki");
                        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Licensed under the GPLv3");
                        sender.sendMessage(ChatColor.GRAY + "Many contributors have written source code and task types for Quests," +
                                " please see the GitHub link for an up-to-date list of contributors.");
                        return true;
                    }
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        showAdminHelp(sender, "moddata");
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        TaskType taskType = null;
                        for (TaskType task : plugin.getTaskTypeManager().getTaskTypes()) {
                            if (task.getType().equals(args[2])) {
                                taskType = task;
                            }
                        }
                        if (taskType == null) {
                            sender.sendMessage(Messages.COMMAND_TASKVIEW_ADMIN_FAIL.getMessage().replace("{task}", args[2]));
                        } else {
                            sender.sendMessage(ChatColor.RED + "Task type: " + ChatColor.GRAY + taskType.getType());
                            sender.sendMessage(ChatColor.RED + "Author: " + ChatColor.GRAY + taskType.getAuthor());
                            sender.sendMessage(ChatColor.RED + "Description: " + ChatColor.GRAY + taskType.getDescription());
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("info")) {
                        Quest quest = plugin.getQuestManager().getQuestById(args[2]);
                        if (quest == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.getMessage().replace("{quest}", args[2]));
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Information for quest '" + quest.getId() + "'");
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE + "Task configurations (" + quest.getTasks().size() + ")");
                            for (Task task : quest.getTasks()) {
                                sender.sendMessage(ChatColor.RED + "Task '" + task.getId() + "':");
                                for (Map.Entry<String, Object> config : task.getConfigValues().entrySet()) {
                                    sender.sendMessage(ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + config.getKey() + ": " + ChatColor.GRAY + ChatColor.ITALIC + config.getValue());
                                }
                            }
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Start string");
                            for (String s : quest.getStartString()) {
                                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                            }
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Reward string");
                            for (String s : quest.getRewardString()) {
                                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                            }
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Rewards");
                            for (String s : quest.getRewards()) {
                                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + s);
                            }
                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE +  "Quest options");
                            sender.sendMessage(ChatColor.RED + "Category: " + ChatColor.GRAY + quest.getCategoryId());
                            sender.sendMessage(ChatColor.RED + "Repeatable: " + ChatColor.GRAY + quest.isRepeatable());
                            sender.sendMessage(ChatColor.RED + "Requirements: " + ChatColor.GRAY + String.join(", ", quest.getRequirements()));
                            sender.sendMessage(ChatColor.RED + "Cooldown enabled: " + ChatColor.GRAY + quest.isCooldownEnabled());
                            sender.sendMessage(ChatColor.RED + "Cooldown time: " + ChatColor.GRAY + quest.getCooldown());
                            sender.sendMessage(ChatColor.RED + "Autostart: " + ChatColor.GRAY + quest.isAutoStartEnabled());
                        }
                        return true;
                    }
                } else if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("q") || args[2].equalsIgnoreCase("resources/bukkit/quests")) {
                            Player player = Bukkit.getPlayer(args[3]);
                            if (player != null) {
                                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                                if (qPlayer != null) {
                                    plugin.getMenuController().openMainMenu(qPlayer);
                                    sender.sendMessage(Messages.COMMAND_QUEST_OPENQUESTS_ADMIN_SUCCESS.getMessage().replace("{player}", player.getName()));
                                    return true;
                                }
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        QPlayer qPlayer = getOtherPlayer(sender, args[3]);
                        if (qPlayer == null) return true;
                        if (args[2].equalsIgnoreCase("fullreset")) {
                            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                            questProgressFile.clear();
                            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
                            if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                                plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_FULLRESET.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        showAdminHelp(sender, "moddata");
                        return true;
                    }
                } else if (args.length == 5) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category")) {
                            if (!plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
                                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                                return true;
                            }
                            Category category = plugin.getQuestManager().getCategoryById(args[4]);
                            if (category == null) {
                                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[4]));
                                return true;
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
                                    return true;
                                }
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        boolean success = false;
                        QPlayer qPlayer = getOtherPlayer(sender, args[3]);
                        if (qPlayer == null) return true;
                        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                        Quest quest = plugin.getQuestManager().getQuestById(args[4]);
                        if (quest == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[4]));
                            //success = true;
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("reset")) {
                            questProgressFile.generateBlankQuestProgress(quest);
                            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_RESET_SUCCESS.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("start")) {
                            QuestStartResult response = qPlayer.startQuest(quest);
                            if (response == QuestStartResult.QUEST_LIMIT_REACHED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLIMIT.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_ALREADY_COMPLETED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOMPLETE.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_COOLDOWN) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_LOCKED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLOCKED.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_ALREADY_STARTED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILSTARTED.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_NO_PERMISSION) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILPERMISSION.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.NO_PERMISSION_FOR_CATEGORY) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                                return true;
                            }
                            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("complete")) {
                            qPlayer.completeQuest(quest);
                            plugin.getPlayerManager().savePlayerSync(qPlayer.getPlayerUUID(), questProgressFile);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS.getMessage().replace("{player}", args[3]).replace("{quest}", quest.getId()));
                            success = true;
                        }
                        if (!success) {
                            showAdminHelp(sender, "moddata");
                        }
                        if (Bukkit.getPlayer(qPlayer.getPlayerUUID()) == null) {
                            plugin.getPlayerManager().dropPlayer(qPlayer.getPlayerUUID());
                        }
                        return true;
                    }
                }
                showAdminHelp(sender, null);
                return true;
            }
            if (sender instanceof Player && (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("resources/bukkit/quests") || args[0].equalsIgnoreCase("quest"))) {
                Player player = (Player) sender;
                if (args.length >= 3) {
                    Quest quest = plugin.getQuestManager().getQuestById(args[1]);
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                        return true;
                    }
                    if (quest == null) {
                        sender.sendMessage(Messages.COMMAND_QUEST_GENERAL_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
                    }
                    if (args[2].equalsIgnoreCase("s") || args[2].equalsIgnoreCase("start")) {
                        qPlayer.startQuest(quest);
                    } else if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("cancel")) {
                        qPlayer.cancelQuest(quest);
                    } else if (args[2].equalsIgnoreCase("t") || args[2].equalsIgnoreCase("track")) {
                        qPlayer.trackQuest(quest);
                    } else {
                        sender.sendMessage(Messages.COMMAND_SUB_DOESNTEXIST.getMessage().replace("{sub}", args[2]));
                    }
                    return true;
                }
            } else if (sender instanceof Player && (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("category"))) {
                if (!plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
                    sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                    return true;
                }
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Category category = plugin.getQuestManager().getCategoryById(args[1]);
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                        return true;
                    }
                    if (category == null) {
                        sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[1]));
                    } else {
                        plugin.getMenuController().openQuestCategory(qPlayer, category, null, false);
                        return true;
                    }
                    return true;
                }
            } else if (sender instanceof Player && (args[0].equalsIgnoreCase("random")) && sender.hasPermission("quests.command.random")) {
                Player player = (Player) sender;
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) {
                    player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                    return true;
                }
                List<Quest> validQuests = new ArrayList<>();
                for (Quest quest : plugin.getQuestManager().getQuests().values()) {
                    if (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                        validQuests.add(quest);
                    }
                }

                if (validQuests.isEmpty()) {
                    player.sendMessage(Messages.QUEST_RANDOM_NONE.getMessage());
                    return true;
                }
                int random = ThreadLocalRandom.current().nextInt(0, validQuests.size());
                qPlayer.startQuest(validQuests.get(random));
                return true;
            } else if (sender instanceof Player && (args[0].equalsIgnoreCase("started"))) {
                Player player = (Player) sender;
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) {
                    player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
                    return true;
                }
                plugin.getMenuController().openStartedQuests(qPlayer);
                return true;
            }
            showHelp(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Only admin commands are available to non-player senders.");
        }
        return true;
    }

    private QPlayer getOtherPlayer(CommandSender sender, String name) {
        OfflinePlayer ofp = Bukkit.getOfflinePlayer(name);
        UUID uuid;
        String username;
        if (ofp.hasPlayedBefore()) {
            uuid = ofp.getUniqueId();
            username = ofp.getName();
        } else {
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", name));
            return null;
        }
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
        if (qPlayer == null) {
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", username));
            plugin.getPlayerManager().loadPlayer(uuid);
            qPlayer = plugin.getPlayerManager().getPlayer(uuid);
        }
        if (qPlayer == null) {
            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", username));
            return null;
        }
        return qPlayer;
    }

    private void showProblems(CommandSender sender) {
        if (!plugin.getConfigProblems().isEmpty()) {
//            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            sender.sendMessage(ChatColor.GRAY + "Detected problems and potential issues:");
            Set<ConfigProblem.ConfigProblemType> problemTypes = new HashSet<>();
            int count = 0;
            for (Map.Entry<String, List<ConfigProblem>> entry : plugin.getConfigProblems().entrySet()) {
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

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests v" + plugin
                .getDescription().getVersion() + " " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/resources/bukkit/quests " + ChatColor.DARK_GRAY + ": show quests");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests c/category <categoryid> " + ChatColor.DARK_GRAY + ": open category by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests q/quest <questid> <start|cancel|track>" + ChatColor.DARK_GRAY + ": start, cancel or track quest by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a/admin " + ChatColor.DARK_GRAY + ": view help for admins");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------=[" + ChatColor.RED + " made with <3 by LMBishop " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=--------");
    }

    private void showAdminHelp(CommandSender sender, String command) {
        if (command != null && command.equalsIgnoreCase("opengui")) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: opengui " + ChatColor
                    .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
            sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui q/quests <player> " + ChatColor.DARK_GRAY + ": forcefully show" +
                    " quests for player");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui c/category <player> <category> " + ChatColor.DARK_GRAY + ": " +
                    "forcefully " +
                    "open category by ID for player");
            sender.sendMessage(ChatColor.GRAY + "These commands are useful for command NPCs. These will bypass the usual quests.command permission.");
        } else if (command != null && command.equalsIgnoreCase("moddata")) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: moddata " + ChatColor
                    .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
            sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata fullreset <player> " + ChatColor.DARK_GRAY + ": clear a " +
                    "players quest data file");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata reset <player> <questid> " + ChatColor.DARK_GRAY + ": clear a " +
                    "players data for specifc quest");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata start <player> <questid> " + ChatColor.DARK_GRAY + ": start a " +
                    "quest for a player");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata complete <player> <questid> " + ChatColor.DARK_GRAY + ": " +
                    "complete a quest for a player");
            sender.sendMessage(ChatColor.GRAY + "These commands modify quest progress for players. Use them cautiously. Changes are irreversible.");
        } else {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin " + ChatColor.GRAY
                    .toString() + ChatColor.STRIKETHROUGH + "]=------------");
            sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui " + ChatColor.DARK_GRAY + ": view help for opengui");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata " + ChatColor.DARK_GRAY + ": view help for quest progression");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a types [type]" + ChatColor.DARK_GRAY + ": view registered task types");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a info [quest]" + ChatColor.DARK_GRAY + ": see information about loaded quests");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a reload " + ChatColor.DARK_GRAY + ": reload Quests configuration");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a config " + ChatColor.DARK_GRAY + ": see detected problems in config");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a itemstack " + ChatColor.DARK_GRAY + ": print information about the current held ItemStack");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a update " + ChatColor.DARK_GRAY + ": check for updates");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a wiki " + ChatColor.DARK_GRAY + ": get a link to the Quests wiki");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a about " + ChatColor.DARK_GRAY + ": get information about Quests");
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----=[" + ChatColor.RED + " requires permission: quests.admin " +
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=-----");
    }

    private List<String> matchTabComplete(String arg, List<String> options) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, options, completions);
        Collections.sort(completions);
        return completions;
    }

    private List<String> tabCompleteCategory(String arg) {
        List<String> options = new ArrayList<>();
        for (Category c : plugin.getQuestManager().getCategories()) {
            options.add(c.getId());
        }
        return matchTabComplete(arg, options);
    }

    private List<String> tabCompleteQuests(String arg) {
        List<String> options = new ArrayList<>(plugin.getQuestManager().getQuests().keySet());
        return matchTabComplete(arg, options);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!plugin.getQuestsConfig().getBoolean("options.tab-completion.enabled")) {
            return null;
        }
        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> options = new ArrayList<>(Arrays.asList("quest", "category", "started"));
                if (sender.hasPermission("quests.admin")) {
                    options.add("admin");
                }
                if (sender.hasPermission("quests.command.random")) {
                    options.add("random");
                }
                return matchTabComplete(args[0], options);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("category")) {
                    return tabCompleteCategory(args[1]);
                } else if (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quest")) {
                    return tabCompleteQuests(args[1]);
                } else if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")
                        && sender.hasPermission("quests.admin")) {
                    List<String> options = Arrays.asList("opengui", "moddata", "types", "reload", "update", "config", "info", "wiki", "about");
                    return matchTabComplete(args[1], options);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quest")
                    && sender.hasPermission("quests.admin")) {
                    Quest q = plugin.getQuestManager().getQuestById(args[1]);
                    if (q != null) {
                        List<String> options = Arrays.asList("start", "cancel", "track");
                        return matchTabComplete(args[2], options);
                    }
                } else if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")
                        && sender.hasPermission("quests.admin")) {
                    if (args[1].equalsIgnoreCase("types")) {
                        List<String> options = new ArrayList<>();
                        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                            options.add(taskType.getType());
                        }
                        return matchTabComplete(args[2], options);
                    } else if (args[1].equalsIgnoreCase("opengui")) {
                        List<String> options = Arrays.asList("resources/bukkit/quests", "category");
                        return matchTabComplete(args[2], options);
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        List<String> options = Arrays.asList("fullreset", "reset", "start", "complete");
                        return matchTabComplete(args[2], options);
                    } else if (args[1].equalsIgnoreCase("info")) {
                        return tabCompleteQuests(args[2]);
                    }
                }
            } else if (args.length == 4) {
                if (sender.hasPermission("quests.admin")) return null;
            } else if (args.length == 5) {
                if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")
                        && sender.hasPermission("quests.admin")) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category")) {
                            return tabCompleteCategory(args[4]);
                        }
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        if (args[2].equalsIgnoreCase("start")
                                || args[2].equalsIgnoreCase("complete")
                                || args[2].equalsIgnoreCase("reset")) {
                            return tabCompleteQuests(args[4]);
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
