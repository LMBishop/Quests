package com.leonardobishop.quests.commands;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

public class CommandQuests implements TabExecutor {

    private final Quests plugin;

    public CommandQuests(Quests plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (plugin.getTaskTypeManager().areRegistrationsAccepted()) {
            sender.sendMessage(ChatColor.RED + "Quests is not ready yet.");
            return true;
        }
        if (plugin.isBrokenConfig() &&
                !(args.length >= 2 &&
                        (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")) &&
                        args[1].equalsIgnoreCase("reload"))) {
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
            qPlayer.openQuests();
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
                        plugin.reloadConfig();
                        plugin.reloadQuests();
                        showProblems(sender);
                        sender.sendMessage(ChatColor.GRAY + "Quests successfully reloaded.");
                        return true;
                    } else if (args[1].equalsIgnoreCase("config")) {
                        showProblems(sender);
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        sender.sendMessage(ChatColor.GRAY + "Registered task types:");
                        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + taskType.getType());
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a types [type].");
                        return true;
                    } else if (args[1].equalsIgnoreCase("info")) {
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
                                sender.sendMessage(plugin.getUpdater().getMessage());
                            } else {
                                sender.sendMessage(ChatColor.GRAY + "No updates were found.");
                            }
                        });
                        return true;
                    } else if (args[1].equalsIgnoreCase("wiki")) {
                        sender.sendMessage(ChatColor.RED + "Link to Quests wiki: " + ChatColor.GRAY + "https://github.com/LMBishop/Quests/wiki");
                        return true;
                    }
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        if (args[2].equalsIgnoreCase("clean")) {
                            FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                                    File playerDataFile = new File(path.toUri());
                                    if (!playerDataFile.getName().toLowerCase().endsWith(".yml")) return FileVisitResult.CONTINUE;
                                    String uuidStr = playerDataFile.getName().replace(".yml", "");
                                    UUID uuid;
                                    try {
                                        uuid = UUID.fromString(uuidStr);
                                    } catch (IllegalArgumentException ex) {
                                        return FileVisitResult.CONTINUE;
                                    }

                                    plugin.getPlayerManager().loadPlayer(uuid);
                                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
                                    qPlayer.getQuestProgressFile().clean();
                                    qPlayer.getQuestProgressFile().saveToDisk(false);
                                    if (Bukkit.getPlayer(uuid) == null) {
                                        plugin.getPlayerManager().dropPlayer(uuid);
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                            };
                            //TODO command to clean specific player
                            try {
                                Files.walkFileTree(Paths.get(plugin.getDataFolder() + File.separator + "playerdata"), fileVisitor);
                            } catch (IOException e) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_CLEAN_FAIL.getMessage());
                                e.printStackTrace();
                                return true;
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_CLEAN_SUCCESS.getMessage());
                            return true;
                        }
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
                        }
                        return true;
                    }
                } else if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("q") || args[2].equalsIgnoreCase("quests")) {
                            Player player = Bukkit.getPlayer(args[3]);
                            if (player != null) {
                                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                                if (qPlayer != null) {
                                    qPlayer.openQuests();
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
                        OfflinePlayer ofp = Bukkit.getOfflinePlayer(args[3]);
                        UUID uuid;
                        String name;
                        // Player.class is a superclass for OfflinePlayer.
                        // getofflinePlayer return a player regardless if exists or not
                        if (ofp.hasPlayedBefore()) {
                            uuid = ofp.getUniqueId();
                            name = ofp.getName();
                        } else {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("fullreset")) {
                            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
                            if (qPlayer == null) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", name));
                                plugin.getPlayerManager().loadPlayer(uuid);
                                qPlayer = plugin.getPlayerManager().getPlayer(uuid); //get again
                            }
                            if (qPlayer == null) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", name));
                                return true;
                            }
                            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                            questProgressFile.clear();
                            questProgressFile.saveToDisk(false);
                            if (Bukkit.getPlayer(uuid) == null) {
                                plugin.getPlayerManager().dropPlayer(uuid);
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_FULLRESET.getMessage().replace("{player}", name));
                            return true;
                        }
                        showAdminHelp(sender, "moddata");
                        return true;
                    }
                } else if (args.length == 5) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category")) {
                            if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
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
                                    if (qPlayer.openCategory(category, null, false) == 0) {
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
                        OfflinePlayer ofp = Bukkit.getOfflinePlayer(args[3]);
                        UUID uuid;
                        String name;
                        if (ofp.hasPlayedBefore()) {
                            uuid = ofp.getUniqueId();
                            name = ofp.getName();
                        } else {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
                        if (qPlayer == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", name));
                            plugin.getPlayerManager().loadPlayer(uuid);
                        }
                        if (qPlayer == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", name));
                            success = true;
                        }
                        qPlayer = plugin.getPlayerManager().getPlayer(uuid); //get again
                        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                        Quest quest = plugin.getQuestManager().getQuestById(args[4]);
                        if (quest == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[4]));
                            //success = true;
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("reset")) {
                            questProgressFile.generateBlankQuestProgress(quest);
                            questProgressFile.saveToDisk(false);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_RESET_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("start")) {
                            QuestStartResult response = qPlayer.startQuest(quest);
                            if (response == QuestStartResult.QUEST_LIMIT_REACHED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLIMIT.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_ALREADY_COMPLETED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOMPLETE.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_COOLDOWN) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_LOCKED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLOCKED.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_ALREADY_STARTED) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILSTARTED.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.QUEST_NO_PERMISSION) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILPERMISSION.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == QuestStartResult.NO_PERMISSION_FOR_CATEGORY) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            }
                            questProgressFile.saveToDisk(false);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("complete")) {
                            qPlayer.completeQuest(quest);
                            questProgressFile.saveToDisk(false);
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        }
                        if (!success) {
                            showAdminHelp(sender, "moddata");
                        }
                        if (Bukkit.getPlayer(uuid) == null) {
                            plugin.getPlayerManager().dropPlayer(uuid);
                        }
                        return true;
                    }
                }
                showAdminHelp(sender, null);
                return true;
            }
            if (sender instanceof Player && (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quests") || args[0].equalsIgnoreCase("quest"))) {
                Player player = (Player) sender;
                if (args.length >= 3) {
                    Quest quest = plugin.getQuestManager().getQuestById(args[1]);
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        sender.sendMessage(ChatColor.RED + "Your quest progress file has not been loaded yet.");
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
                if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
                    sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                    return true;
                }
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Category category = plugin.getQuestManager().getCategoryById(args[1]);
                    if (category == null) {
                        sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[1]));
                    } else {
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                        qPlayer.openCategory(category, null, false);
                        return true;
                    }
                    return true;
                }
            } else if (sender instanceof Player && (args[0].equalsIgnoreCase("random")) && sender.hasPermission("quests.command.random")) {
                Player player = (Player) sender;
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Your quest progress file has not been loaded yet.");
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
                qPlayer.openStartedQuests();
                return true;
            }
            showHelp(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Only admin commands are available to non-player senders.");
        }
        return true;
    }

    private void showProblems(CommandSender sender) {
        if (!plugin.getQuestsConfigLoader().getFilesWithProblems().isEmpty()) {
//            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            sender.sendMessage(ChatColor.GRAY + "Detected problems and potential issues:");
            Set<QuestsConfigLoader.ConfigProblemType> problemTypes = new HashSet<>();
            for (Map.Entry<String, List<QuestsConfigLoader.ConfigProblem>> entry : plugin.getQuestsConfigLoader().getFilesWithProblems().entrySet()) {
                HashMap<QuestsConfigLoader.ConfigProblemType, List<QuestsConfigLoader.ConfigProblem>> sortedProblems = new HashMap<>();
                for (QuestsConfigLoader.ConfigProblem problem : entry.getValue()) {
                    if (sortedProblems.containsKey(problem.getType())) {
                        sortedProblems.get(problem.getType()).add(problem);
                    } else {
                        List<QuestsConfigLoader.ConfigProblem> specificProblems = new ArrayList<>();
                        specificProblems.add(problem);
                        sortedProblems.put(problem.getType(), specificProblems);
                    }
                    problemTypes.add(problem.getType());
                }
                QuestsConfigLoader.ConfigProblemType highest = null;
                for (QuestsConfigLoader.ConfigProblemType type : QuestsConfigLoader.ConfigProblemType.values()) {
                    if (sortedProblems.containsKey(type)) {
                        highest = type;
                        break;
                    }
                }
                ChatColor highestColor = ChatColor.WHITE;
                if (highest != null) {
                    highestColor = highest.getColor();
                }
                sender.sendMessage(highestColor + entry.getKey() + ChatColor.DARK_GRAY + " ----");
                for (QuestsConfigLoader.ConfigProblemType type : QuestsConfigLoader.ConfigProblemType.values()) {
                    if (sortedProblems.containsKey(type)) {
                        for (QuestsConfigLoader.ConfigProblem problem : sortedProblems.get(type)) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " | - " + problem.getType().getColor()
                                    + problem.getType().getShortened() + ChatColor.DARK_GRAY + ": "
                                    + ChatColor.GRAY + problem.getDescription() + ChatColor.DARK_GRAY + " :" + problem.getLocation());
                        }
                    }
                }
            }
//                            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            List<String> legend = new ArrayList<>();
            for (QuestsConfigLoader.ConfigProblemType type : QuestsConfigLoader.ConfigProblemType.values()) {
                if (problemTypes.contains(type))
                    legend.add(type.getColor() + type.getShortened() + ChatColor.DARK_GRAY + " = " + type.getColor() + type.getTitle());
            }
            sender.sendMessage(ChatColor.DARK_GRAY.toString() + "----");
            sender.sendMessage(ChatColor.GRAY.toString() + plugin.getQuestsConfigLoader().getProblemsCount() + " problem(s) | " + String.join(ChatColor.DARK_GRAY + ", ", legend));
        } else {
            sender.sendMessage(ChatColor.GRAY + "Quests did not detect any problems with your configuration.");
        }
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests v" + plugin
                .getDescription().getVersion() + " " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests " + ChatColor.DARK_GRAY + ": show quests");
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
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata clean " + ChatColor.DARK_GRAY + ": " +
                    "clean quest data files for quests which are no longer defined");
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
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a update " + ChatColor.DARK_GRAY + ": check for updates");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a wiki " + ChatColor.DARK_GRAY + ": get a link to the Quests wiki");
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
        if (!Options.TAB_COMPLETE_ENABLED.getBooleanValue(true)) {
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
                    List<String> options = Arrays.asList("opengui", "moddata", "types", "reload", "update", "config", "info", "wiki");
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
                        List<String> options = Arrays.asList("quests", "category");
                        return matchTabComplete(args[2], options);
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        List<String> options = Arrays.asList("fullreset", "reset", "start", "complete", "clean");
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
