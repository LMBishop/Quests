package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestsCommandTabCompleter implements TabCompleter {

    private BukkitQuestsPlugin plugin;

    public QuestsCommandTabCompleter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
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
                if (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("category") || (args[0].equalsIgnoreCase("random") && sender.hasPermission("quests.command.random"))) {
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
                        List<String> options = Arrays.asList("quests", "category");
                        return matchTabComplete(args[2], options);
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        List<String> options = Arrays.asList("fullreset", "reset", "start", "complete", "random");
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
                        if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category") || args[2].equalsIgnoreCase("random")) {
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
