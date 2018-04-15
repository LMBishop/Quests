package me.fatpigsarefat.quests.commands;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.obj.Messages;
import me.fatpigsarefat.quests.obj.Options;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.quests.Category;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.tasktypes.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandQuests implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (Quests.getInstance().isBrokenConfig()) {
            sender.sendMessage(ChatColor.RED + "You have a YAML error in your config and Quests cannot load. If this is your first time using Quests, please " +
                    "delete the Quests folder and RESTART (not reload!) the server. If you have modified the config, check for errors in a YAML parser.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
            qPlayer.openQuests();
            return true;
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin") && sender.hasPermission("quests.admin")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        showAdminHelp(sender, "moddata");
                        return true;
                    } else if (args[1].equalsIgnoreCase("reload")) {
                        Quests.getInstance().reloadConfig();
                        Quests.getInstance().reloadQuests();
                        sender.sendMessage(ChatColor.GRAY + "Quests was reloaded.");
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        sender.sendMessage(ChatColor.GRAY + "Registered task types:");
                        for (TaskType taskType : Quests.getTaskTypeManager().getTaskTypes()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + taskType.getType());
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a types [type].");
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
                        for (TaskType task : Quests.getTaskTypeManager().getTaskTypes()) {
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
                                QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
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
                        showAdminHelp(sender, "moddata");
                        return true;
                    }
                } else if (args.length == 5) {
                    if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category")) {
                        if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
                            sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                            return true;
                        }
                        Category category = Quests.getQuestManager().getCategoryById(args[4]);
                        if (category == null) {
                            sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[4]));
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[3]);
                        if (player != null) {
                            QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
                            if (qPlayer != null) {
                                qPlayer.openCategory(category);
                                sender.sendMessage(Messages.COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS.getMessage().replace("{player}", player.getName())
                                        .replace("{category}", category.getId()));
                                return true;
                            }
                        }
                        sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                        return true;
                    }
                }

                showAdminHelp(sender, null);
                return true;
            }
            if (sender instanceof Player && (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quests"))) {
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Quest quest = Quests.getQuestManager().getQuestById(args[1]);
                    if (quest == null) {
                        sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
                    } else {
                        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
                        if (qPlayer == null) {
                            // shit + fan
                            sender.sendMessage(ChatColor.RED + "An error occurred finding your player.");
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
                    Category category = Quests.getQuestManager().getCategoryById(args[1]);
                    if (category == null) {
                        sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[1]));
                    } else {
                        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
                        qPlayer.openCategory(category);
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
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------=[" + ChatColor.RED + " Quests v" + Quests.getInstance()
                .getDescription().getVersion() + " " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=------------");
        sender.sendMessage(ChatColor.GRAY + "The following commands are available: ");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests " + ChatColor.DARK_GRAY + ": show quests");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests c/category <categoryid> " + ChatColor.DARK_GRAY + ": open category by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests q/quest <questid> " + ChatColor.DARK_GRAY + ": start quest by ID");
        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + "/quests a/admin " + ChatColor.DARK_GRAY + ": view help for admins");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----=[" + ChatColor.RED + " made with <3 by fatpigsarefat " + ChatColor
                .GRAY.toString() + ChatColor.STRIKETHROUGH + "]=-----");
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
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----=[" + ChatColor.RED + " requires permission: quests.admin " +
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "]=-----");
    }
}
