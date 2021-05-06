package com.leonardobishop.quests.commands;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.api.enums.StoreType;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CommandQuests implements CommandExecutor {

    private final Quests plugin;

    public CommandQuests(Quests plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (plugin.isBrokenConfig()) {
            sender.sendMessage(ChatColor.RED + "The main config must be in tact before quests can be used. Quests has failed to load the following files:");
            for (Map.Entry<String, QuestsConfigLoader.ConfigLoadError> entry : plugin.getQuestsConfigLoader().getBrokenFiles().entrySet()) {
                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + entry.getKey() + ": " + ChatColor.GRAY + entry.getValue().getMessage());
            }
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "If this is your first time using Quests, please delete the Quests folder and RESTART (not reload!) the server.");
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
                        if (!plugin.getQuestsConfigLoader().getBrokenFiles().isEmpty()) {
                            sender.sendMessage(ChatColor.RED + "Quests has failed to load the following files:");
                            for (Map.Entry<String, QuestsConfigLoader.ConfigLoadError> entry : plugin.getQuestsConfigLoader().getBrokenFiles().entrySet()) {
                                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + entry.getKey() + ": " + ChatColor.GRAY + entry.getValue().getMessage());
                            }
                            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "If this is your first time using Quests, please delete the Quests folder and RESTART (not reload!) the server.");
                        } else {
                            sender.sendMessage(ChatColor.GRAY + "Quests was reloaded.");
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        sender.sendMessage(ChatColor.GRAY + "Registered task types:");
                        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + taskType.getType());
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a types [type].");
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
                        if (ofp != null) {
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
                                plugin.getPlayerManager().loadPlayer(uuid, true, plugin.getDatabase().getStoreType());
                                qPlayer = plugin.getPlayerManager().getPlayer(uuid); //get again
                            }
                            if (qPlayer == null) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", name));
                                return true;
                            }
                            QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                            questProgressFile.clear();
                            questProgressFile.saveToDisk(plugin.getDatabase().getStoreType());
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_FULLRESET.getMessage().replace("{player}", name));
                            return true;
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid).isOnlyDataLoaded()) {
                            plugin.getPlayerManager().removePlayer(uuid);
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
                        if (ofp != null) {
                            uuid = ofp.getUniqueId();
                            name = ofp.getName();
                        } else {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(uuid);
                        if (qPlayer == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", name));
                            plugin.getPlayerManager().loadPlayer(uuid, true, plugin.getDatabase().getStoreType());
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
                            questProgressFile.generateBlankQuestProgress(quest.getId());
                            questProgressFile.saveToDisk(plugin.getDatabase().getStoreType());
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_RESET_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("start")) {
                            QuestStartResult response = questProgressFile.startQuest(quest);
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
                            questProgressFile.saveToDisk(plugin.getDatabase().getStoreType());
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("complete")) {
                            questProgressFile.completeQuest(quest);
                            questProgressFile.saveToDisk(plugin.getDatabase().getStoreType());
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid).isOnlyDataLoaded()) {
                            plugin.getPlayerManager().removePlayer(uuid);
                        }
                        if (!success) {
                            showAdminHelp(sender, "moddata");
                        }
                        return true;
                    }
                }
                showAdminHelp(sender, null);
                return true;
            }
            if (sender instanceof Player && (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quests"))) {
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Quest quest = plugin.getQuestManager().getQuestById(args[1]);
                    if (quest == null) {
                        sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
                    } else {
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                        if (qPlayer == null) {
                            // shit + fan
                            sender.sendMessage(ChatColor.RED + "An error occurred finding your player."); //lazy? :)
                        } else {
                            qPlayer.getQuestProgressFile().startQuest(quest);
                        }
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
            }
            showHelp(sender);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Only admin commands are available to non-player senders.");
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests v" + plugin
                .getDescription().getVersion() + " " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests " + ChatColor.DARK_GRAY + ": show quests");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests c/category <categoryid> " + ChatColor.DARK_GRAY + ": open category by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests q/quest <questid> " + ChatColor.DARK_GRAY + ": start quest by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a/admin " + ChatColor.DARK_GRAY + ": view help for admins");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------=[" + ChatColor.RED + " made with <3 by LMBishop " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=--------");
    }

    private void showAdminHelp(CommandSender sender, String command) {
        if (command != null && command.equalsIgnoreCase("opengui")) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin: opengui " + ChatColor
                    .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
            sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui q/quest <player> " + ChatColor.DARK_GRAY + ": forcefully show" +
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
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata reset <player> <questid>" + ChatColor.DARK_GRAY + ": clear a " +
                    "players data for specifc quest");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata start <player> <questid>" + ChatColor.DARK_GRAY + ": start a " +
                    "quest for a player");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata complete <player> <questid>" + ChatColor.DARK_GRAY + ": " +
                    "complete a quest for a player");
            sender.sendMessage(ChatColor.GRAY + "These commands modify quest progress for players. Use them cautiously. Changes are irreversible.");
        } else {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests Admin " + ChatColor.GRAY
                    .toString() + ChatColor.STRIKETHROUGH + "]=------------");
            sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a opengui " + ChatColor.DARK_GRAY + ": view help for opengui");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a moddata " + ChatColor.DARK_GRAY + ": view help for quest progression");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a types [type]" + ChatColor.DARK_GRAY + ": view registered task types");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a reload " + ChatColor.DARK_GRAY + ": reload Quests configuration");
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a update " + ChatColor.DARK_GRAY + ": check for updates");
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----=[" + ChatColor.RED + " requires permission: quests.admin " +
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=-----");
    }
}
