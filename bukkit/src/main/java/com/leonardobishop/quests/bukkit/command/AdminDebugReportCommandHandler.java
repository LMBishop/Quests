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
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class AdminDebugReportCommandHandler implements CommandHandler {

    private static Method getMinecraftServerMethod;

    static {
        try {
            getMinecraftServerMethod = Server.class.getMethod("getMinecraftVersion");
        } catch (NoSuchMethodException ignored) {
            // introduced in one of 1.15.x versions
        }
    }

    private final BukkitQuestsPlugin plugin;

    private List<String> errors;
    private CommandSender currentReportGenerator;

    public AdminDebugReportCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (currentReportGenerator != null) {
            sender.sendMessage(ChatColor.RED + "A report is already being generated. Please wait until it is finished.");
            return;
        }

        errors = new ArrayList<>();
        currentReportGenerator = sender;

        try {
            sender.sendMessage(ChatColor.GRAY + "Generating Quests debug report...");
            if (!plugin.getLogHistory().isEnabled()) {
                sender.sendMessage(ChatColor.RED + "Log history is not enabled. Quests will only generate a basic debug report. " +
                        "If you were asked to generate one with log history, this must be turned on in your configuration.");
            }
            long start = System.currentTimeMillis();
            Path path = new File(plugin.getDataFolder() + File.separator + "debug" + File.separator + "debug_" + start + ".txt").toPath();
            try {
                Path directory = new File(plugin.getDataFolder() + File.separator + "debug").toPath();
                Files.createDirectories(directory);
                Files.createFile(path);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Failed to create debug file!");
                e.printStackTrace();
                return;
            }

            List<String> lines = new ArrayList<>();
            lines.add("################################");
            lines.add("#      Server Information      #");
            lines.add("################################");
            lines.add("");
            lines.add("Server name: " + plugin.getServer().getName());
            lines.add("Server version: " + plugin.getServer().getVersion());
            lines.add("Bukkit version: " + plugin.getServer().getBukkitVersion());
            if (getMinecraftServerMethod != null) {
                try {
                    lines.add("Minecraft version: " + getMinecraftServerMethod.invoke(plugin.getServer()));
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
            }
            lines.add("Player count: " + plugin.getServer().getOnlinePlayers().size());
            lines.add("");

            lines.add("################################");
            lines.add("#      Quests Information      #");
            lines.add("################################");
            lines.add("");
            lines.add("Quests version: " + plugin.getDescription().getVersion());
            lines.add("Valid configuration: " + plugin.isValidConfiguration());
            lines.add("");
            printList(lines, 0, "Task types available", plugin.getTaskTypeManager().getTaskTypes(), TaskType::getType);
            lines.add("");
            printList(lines, 0, "Quests", plugin.getQuestManager().getQuestMap().values(), Quest::getId);
            lines.add("");
            printList(lines, 0, "Categories", plugin.getQuestManager().getCategories(), Category::getId);
            lines.add("");
            BukkitQuestCompleter completer = (BukkitQuestCompleter) plugin.getQuestCompleter();
            printList(lines, 0, "Completion queue", completer.getCompletionQueue(), questProgress -> questProgress.getPlayerUUID().toString());
            lines.add("");
            printList(lines, 0, "Full check queue", completer.getFullCheckQueue(), questProgressFile -> questProgressFile.getPlayerUUID().toString());
            lines.add("");
            printList(lines, 0, "Expired check queue", completer.getExpiredCheckQueue(), UUID::toString);
            lines.add("");

            lines.add("################################");
            lines.add("#           Storage            #");
            lines.add("################################");
            lines.add("");
            lines.add("Storage provider: " + plugin.getStorageProvider().getName());
            lines.add("");

            lines.add("################################");
            lines.add("#             Hook             #");
            lines.add("################################");
            lines.add("");
            lines.add("Core protect hook: " + (plugin.getCoreProtectHook() != null));
            lines.add("Essentials hook: " + (plugin.getEssentialsHook() != null));
            lines.add("PlaceholderAPI hook: " + (plugin.getPlaceholderAPIHook() != null));
            lines.add("Item getter: " + plugin.getItemGetter().getClass().getSimpleName());
            lines.add("Title handler: " + plugin.getTitleHandle().getClass().getSimpleName());
            lines.add("Version specific handler: " + plugin.getVersionSpecificHandler().getClass().getSimpleName());
            lines.add("");

            lines.add("################################");
            lines.add("#           Options            #");
            lines.add("################################");
            lines.add("");
            if (plugin.isValidConfiguration()) {
                lines.add("GUI use placeholder API: " + plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi", false));
                lines.add("Quests use placeholder API: " + plugin.getQuestsConfig().getBoolean("options.quests-use-placeholderapi", false));
                lines.add("Quests autostart: " + plugin.getQuestsConfig().getBoolean("options.quest-autostart", false));
                lines.add("Quests autotrack: " + plugin.getQuestsConfig().getBoolean("options.quest-autotrack", true));
                lines.add("Verify quests exist on load: " + plugin.getQuestsConfig().getBoolean("options.verify-quest-exists-on-load", true));
                lines.add("Queue executor interval: " + plugin.getQuestsConfig().getInt("options.performance-tweaking.quest-queue-executor-interval", 1) + " ticks");
                lines.add("Autosave interval: " + plugin.getQuestsConfig().getInt("options.performance-tweaking.quest-autosave-interval", 12000) + " ticks");
                lines.add("Override errors: " + plugin.getQuestsConfig().getBoolean("options.error-checking.override-errors", false));
                lines.add("Placeholder cache time: " + plugin.getQuestsConfig().getInt("options.placeholder-cache-time") + " seconds");
                lines.add("Quest mode: " + plugin.getQuestsConfig().getInt("quest-mode.mode"));
            } else {
                lines.add("Configuration unavailable.");
            }
            lines.add("");

            lines.add("################################");
            lines.add("#            Items             #");
            lines.add("################################");
            lines.add("");
            lines.add("Number of items: " + plugin.getQuestItemRegistry().getAllItems().size());
            lines.add("");
            for (QuestItem questItem : plugin.getQuestItemRegistry().getAllItems()) {
                Map<String, Object> values = getFieldValues(questItem.getClass(), questItem);
                values.putAll(getFieldValues(questItem.getClass().getSuperclass(), questItem));
                printMap(lines, 0, "Item " + questItem.getId() + " (" + questItem.getClass().getSimpleName() + ")", values);
                lines.add("");
            }

            lines.add("################################");
            lines.add("#    Configuration Problems    #");
            lines.add("################################");
            lines.add("");
            lines.add("Number of problems: " + plugin.getConfigProblems().size());
            lines.add("");
            for (Map.Entry<String, List<ConfigProblem>> entry : plugin.getConfigProblems().entrySet()) {
                String id = entry.getKey();
                List<ConfigProblem> problems = entry.getValue();

                printList(lines, 0, "Problems for '" + id + "'", problems,
                        (ConfigProblem problem) -> String.format("%s: %s (:%s)", problem.getType(), problem.getDescription(), problem.getLocation()));
                lines.add("");
            }

            lines.add("################################");
            lines.add("#            Quests            #");
            lines.add("################################");
            lines.add("");
            lines.add("Number of quests: " + plugin.getQuestManager().getQuestMap().size());
            lines.add("");
            for (Quest quest : plugin.getQuestManager().getQuestMap().values()) {
                Map<String, Object> questValues = getFieldValues(quest.getClass(), quest, "tasks", "tasksByType");
                try {
                    Field tasksField = quest.getClass().getDeclaredField("tasks");
                    tasksField.setAccessible(true);
                    Map<String, Task> tasksMap = (Map<String, Task>) tasksField.get(quest);
                    Map<String, Object> tasksValues = new HashMap<>();
                    for (Map.Entry<String, Task> taskEntry : tasksMap.entrySet()) {
                        Task task = taskEntry.getValue();
                        tasksValues.put(task.getId(), getFieldValues(task.getClass(), task));
                    }
                    questValues.put("tasks", tasksValues);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    error("Failed to get tasks for quest " + quest.getId() + ": " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")");
                    e.printStackTrace();
                }

                printMap(lines, 0, "Quest '" + quest.getId() + "'", questValues);
                lines.add("");
            }

            lines.add("################################");
            lines.add("#           Players            #");
            lines.add("################################");
            lines.add("");
            printList(lines, 0, "Players online", Bukkit.getOnlinePlayers(), player -> player.getUniqueId().toString());
            lines.add("");
            printList(lines, 0, "QPlayers loaded", plugin.getPlayerManager().getQPlayers(), qPlayer -> qPlayer.getPlayerUUID().toString());
            lines.add("");
            for (QPlayer qPlayer : plugin.getPlayerManager().getQPlayers()) {
                lines.add("QPlayer " + qPlayer.getPlayerUUID() + ":");
                QPlayerPreferences preferences = qPlayer.getPlayerPreferences();
                printMap(lines, 1, "Preferences", getFieldValues(preferences.getClass(), preferences));

                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                Map<String, QuestProgress> questProgressMap = questProgressFile.getQuestProgressMap();
                Map<String, Object> questProgressValues = new LinkedHashMap<>();
                for (Map.Entry<String, QuestProgress> entry : questProgressMap.entrySet()) {
                    QuestProgress questProgress = entry.getValue();
                    Map<String, Object> questProgressValue = getFieldValues(questProgress.getClass(), questProgress, "plugin", "taskProgressMap");

                    Map<String, TaskProgress> taskProgressMap = questProgress.getTaskProgressMap();
                    Map<String, Object> taskProgressValues = new LinkedHashMap<>();
                    for (Map.Entry<String, TaskProgress> taskEntry : taskProgressMap.entrySet()) {
                        TaskProgress taskProgress = taskEntry.getValue();
                        taskProgressValues.put(taskEntry.getKey(), getFieldValues(taskProgress.getClass(), taskProgress, "plugin", "questProgress"));
                    }
                    questProgressValue.put("taskProgress", taskProgressValues);

                    questProgressValues.put(entry.getKey(), questProgressValue);
                }
                printMap(lines, 1, "Quest progress", questProgressValues);
                lines.add("    Quest controller: " + qPlayer.getQuestController().getName());
                lines.add("");
            }

            if (plugin.getLogHistory().isEnabled()) {
                lines.add("################################");
                lines.add("#         Log History          #");
                lines.add("################################");
                lines.add("");
                int timeMaxLength = 1;
                int typeMaxLength = 1;
                int threadMaxLength = 1;
                for (LogHistory.LogEntry line : plugin.getLogHistory().getEntries()) {
                    timeMaxLength = Math.max(timeMaxLength, String.valueOf(line.getTime()).length());
                    typeMaxLength = Math.max(typeMaxLength, line.getType().toString().length());
                    threadMaxLength = Math.max(threadMaxLength, line.getThread().length());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (LogHistory.LogEntry line : plugin.getLogHistory().getEntries()) {
                    lines.add(String.format("%-" + timeMaxLength + "s %-" + typeMaxLength + "s %-" + threadMaxLength + "s | %s",
                            dateFormat.format(new Date(line.getTime())), line.getType().toString(), line.getThread(), line.getEntry()));
                }
            }

            List<String> errors = new ArrayList<>();
            lines.add(0, "");
            lines.add(0, "Log history: " + plugin.getLogHistory().isEnabled());
            printList(errors, 0, "Errors generating report", this.errors, String::valueOf);
            lines.addAll(0, errors);
            lines.add(0, "Time taken: " + (System.currentTimeMillis() - start) + "ms");
            lines.add(0, "Generated at: " + new Date(start) + " (" + start + ")");
            lines.add(0, "");
            lines.add(0, "################################");
            lines.add(0, "#      Report Information      #");
            lines.add(0, "################################");
            try {
                Files.write(path, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                error("Failed to write report to " + path + "!");
                e.printStackTrace();
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "Debug file created at " + path + ". (Took " + (System.currentTimeMillis() - start) + "ms.)");
        } finally {
            currentReportGenerator = null;
            errors = new ArrayList<>();
        }
    }

    private Map<String, Object> getFieldValues(Class<?> clazz, Object object, String... excludeFields) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> values = new LinkedHashMap<>();
        for (Field field : fields) {
            if (Arrays.asList(excludeFields).contains(field.getName())) {
                continue;
            }
            try {
                field.setAccessible(true);
                values.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                error("Failed to get field value for " + clazz.getSimpleName() + "." + field.getName() + ": " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")");
                e.printStackTrace();
            }
        }
        return values;
    }

    private <E> void printList(List<String> lines, int depth, String title, Collection<E> list) {
        printList(lines, depth, title, list, Object::toString);
    }

    private <E> void printList(List<String> lines, int depth, String title, Collection<E> list, Function<E, String> getter) {
        depth = depth * 4;
        if (list.size() == 0) {
            lines.add(String.format("%s%s (0): (empty)", " ".repeat(depth), title));
            return;
        }
        lines.add(String.format("%s%s (%d):", " ".repeat(depth), title, list.size()));
        for (E element : list) {
            lines.add(String.format("%s - %s", " ".repeat(depth), getter.apply(element)));
        }
    }

    private <K, V> void printMap(List<String> lines, int depth, String title, Map<K, V> map) {
        depth = depth * 4;
        if (map.size() == 0) {
            lines.add(String.format("%s%s (0): (empty)", " ".repeat(depth), title));
            return;
        }
        lines.add(String.format("%s%s:", " ".repeat(depth), title));
        int keyMaxLength = 1;
        int valueMaxLength = 1;
        Map<String, String> stringifiedValues = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            if (key.length() > keyMaxLength) {
                keyMaxLength = key.length();
            }
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            String value;
            if (entry.getValue() instanceof Map) {
                List<String> subLines = new ArrayList<>();
                printMap(subLines, 0, "Map", (Map<?, ?>) entry.getValue());
                value = String.join("\n", subLines);
            } else if (entry.getValue() instanceof List) {
                List<String> subLines = new ArrayList<>();
                printList(subLines, 0, "List", (Collection<?>) entry.getValue());
                value = String.join("\n", subLines);
            } else {
                value = String.valueOf(entry.getValue());
            }

            for (String line : value.split("\n")) {
                if (line.length() > valueMaxLength) {
                    valueMaxLength = line.length();
                }
            }
            stringifiedValues.put(String.valueOf(entry.getKey()), value);
        }
        String separator = String.format("%s|-%-" + keyMaxLength + "s-+-%-" + valueMaxLength + "s-|", " ".repeat(depth), "-".repeat(keyMaxLength), "-".repeat(valueMaxLength));
        lines.add(separator);
        for (Map.Entry<String, String> entry : stringifiedValues.entrySet()) {
            String value = entry.getValue();
            boolean firstLine = true;
            for (String line : value.split("\n")) {
                lines.add(String.format("%s| %-" + keyMaxLength + "s | %-" + valueMaxLength + "s |", " ".repeat(depth), firstLine ? entry.getKey() : "", line));
                firstLine = false;
            }
            lines.add(separator);
        }
    }

    private void error(String error) {
        errors.add(error);
        currentReportGenerator.sendMessage(ChatColor.RED + error);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
