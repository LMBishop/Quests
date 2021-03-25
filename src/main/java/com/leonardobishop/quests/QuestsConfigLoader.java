package com.leonardobishop.quests;

import com.leonardobishop.quests.hooks.itemgetter.ItemGetter;
import com.leonardobishop.quests.menu.QItemStack;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.util.Options;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestsConfigLoader {

    private final Map<String, List<ConfigProblem>> filesWithProblems = new HashMap<>();
    private final Quests plugin;
    private int problemsCount;

    public QuestsConfigLoader(Quests plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads and parses config into memory including the quests folder.
     */
    public void loadConfig() {
        plugin.reloadConfig();
        Options.invalidateCaches();
        filesWithProblems.clear();
        plugin.setBrokenConfig(false);

        // test CONFIG file integrity
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        } catch (Exception ex) {
            filesWithProblems.put("<MAIN CONFIG> config.yml", Collections.singletonList(new ConfigProblem(ConfigProblemType.ERROR, ConfigProblemDescriptions.MALFORMED_YAML.getDescription())));
            plugin.setBrokenConfig(true);
        }


        if (!plugin.isBrokenConfig()) {
            HashMap<String, Map<String, Object>> globalTaskConfig = new HashMap<>();
            for (String id : plugin.getConfig().getConfigurationSection("categories").getKeys(false)) {
                ItemStack displayItem = plugin.getItemStack("categories." + id + ".display", plugin.getConfig());
                boolean permissionRequired = plugin.getConfig().getBoolean("categories." + id + ".permission-required", false);

                Category category = new Category(id, displayItem, permissionRequired);
                plugin.getQuestManager().registerCategory(category);
            }
            plugin.getQuestsLogger().setServerLoggingLevel(QuestsLogger.LoggingLevel.fromNumber(plugin.getConfig().getInt("options.verbose-logging-level", 2)));

            HashMap<String, Quest> pathToQuest = new HashMap<>();

            if (plugin.getConfig().isConfigurationSection("global-task-configuration.types")) {
                for (String type : plugin.getConfig().getConfigurationSection("global-task-configuration.types").getKeys(false)) {
                    HashMap<String, Object> configValues = new HashMap<>();
                    for (String key : plugin.getConfig().getConfigurationSection("global-task-configuration.types." + type).getKeys(false)) {
                        configValues.put(key, plugin.getConfig().get("global-task-configuration.types." + type + "." + key));
                    }
                    globalTaskConfig.putIfAbsent(type, configValues);
                }
            }

            FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
                final URI questsRoot = Paths.get(plugin.getDataFolder() + File.separator + "quests").toUri();

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                    try {
                        File questFile = new File(path.toUri());
                        URI relativeLocation = questsRoot.relativize(path.toUri());

                        if (!questFile.getName().toLowerCase().endsWith(".yml")) return FileVisitResult.CONTINUE;

                        YamlConfiguration config = new YamlConfiguration();
                        // test QUEST file integrity
                        try {
                            config.load(questFile);
                        } catch (Exception ex) {
                            filesWithProblems.put(relativeLocation.getPath(), Collections.singletonList(new ConfigProblem(ConfigProblemType.ERROR, ConfigProblemDescriptions.MALFORMED_YAML.getDescription())));
                            return FileVisitResult.CONTINUE;
                        }

                        String id = questFile.getName().replace(".yml", "");

                        List<ConfigProblem> configProblems = new ArrayList<>();

                        if (!StringUtils.isAlphanumeric(id)) {
                            configProblems.add(new ConfigProblem(ConfigProblemType.ERROR, ConfigProblemDescriptions.INVALID_QUEST_ID.getDescription(id)));
                        }

                        // CHECK EVERYTHING WRONG WITH THE QUEST FILE BEFORE ACTUALLY LOADING THE QUEST

                        if (!config.isConfigurationSection("tasks")) {
                            configProblems.add(new ConfigProblem(ConfigProblemType.ERROR, ConfigProblemDescriptions.NO_TASKS.getDescription(), "tasks"));
                        } else { //continue
                            int validTasks = 0;
                            for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                                boolean isValid = true;
                                String taskRoot = "tasks." + taskId;
                                String taskType = config.getString(taskRoot + ".type");

                                if (!config.isConfigurationSection(taskRoot)) {
                                    configProblems.add(new ConfigProblem(ConfigProblemType.WARNING, ConfigProblemDescriptions.TASK_MALFORMED_NOT_SECTION.getDescription(taskId), taskRoot));
                                    continue;
                                }

                                if (taskType == null) {
                                    configProblems.add(new ConfigProblem(ConfigProblemType.WARNING, ConfigProblemDescriptions.NO_TASK_TYPE.getDescription(), taskRoot));
                                    continue;
                                }

                                // check the tasks
                                TaskType t = plugin.getTaskTypeManager().getTaskType(taskType);
                                if (t != null) {
                                    HashMap<String, Object> configValues = new HashMap<>();
                                    for (String key : config.getConfigurationSection(taskRoot).getKeys(false)) {
                                        configValues.put(key, config.get(taskRoot + "." + key));
                                    }

                                    configProblems.addAll(t.detectProblemsInConfig(taskRoot, configValues));
                                } else {
                                    configProblems.add(new ConfigProblem(ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_TASK_TYPE.getDescription(taskType), taskRoot));
                                    isValid = false;
                                }

                                if (isValid) {
                                    validTasks++;
                                }
                            }
                            if (validTasks == 0) {
                                configProblems.add(new ConfigProblem(ConfigProblemType.ERROR, ConfigProblemDescriptions.NO_TASKS.getDescription(), "tasks"));
                            }
                        }

                        boolean error = false;
                        for (ConfigProblem problem : configProblems) {
                            if (problem.getType() == ConfigProblemType.ERROR) {
                                error = true;
                                break;
                            }
                        }

                        // END OF THE CHECKING
                        if (!error && !Options.ERROR_CHECKING_OVERRIDE.getBooleanValue(false)) {
                            QItemStack displayItem = getQItemStack("display", config);
                            List<String> rewards = config.getStringList("rewards");
                            List<String> requirements = config.getStringList("options.requires");
                            List<String> rewardString = config.getStringList("rewardstring");
                            List<String> startString = config.getStringList("startstring");
                            boolean repeatable = config.getBoolean("options.repeatable", false);
                            boolean cooldown = config.getBoolean("options.cooldown.enabled", false);
                            boolean permissionRequired = config.getBoolean("options.permission-required", false);
                            int cooldownTime = config.getInt("options.cooldown.time", 10);
                            int sortOrder = config.getInt("options.sort-order", 1);
                            String category = config.getString("options.category");
                            Map<String, String> placeholders = new HashMap<>();

                            if (category == null) category = "";

                            Quest quest;
                            if (category.equals("")) {
                                quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString, startString, placeholders, sortOrder);
                            } else {
                                quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString, startString, placeholders, category, sortOrder);
                                Category c = plugin.getQuestManager().getCategoryById(category);
                                if (c != null) {
                                    c.registerQuestId(id);
                                } else {
                                    configProblems.add(new ConfigProblem(ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_CATEGORY.getDescription(category), "options.category"));
                                }
                            }

                            for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                                String taskRoot = "tasks." + taskId;
                                String taskType = config.getString(taskRoot + ".type");

                                Task task = new Task(taskId, taskType);

                                for (String key : config.getConfigurationSection(taskRoot).getKeys(false)) {
                                    task.addConfigValue(key, config.get(taskRoot + "." + key));
                                }

                                if (globalTaskConfig.containsKey(taskType)) {
                                    for (Map.Entry<String, Object> entry : globalTaskConfig.get(taskType).entrySet()) {
                                        if (Options.GLOBAL_TASK_CONFIGURATION_OVERRIDE.getBooleanValue() && task.getConfigValue(entry.getKey()) != null)
                                            continue;
                                        task.addConfigValue(entry.getKey(), entry.getValue());
                                    }
                                }

                                quest.registerTask(task);
                            }


                            for (String line : displayItem.getLoreNormal()) {
                                findInvalidTaskReferences(quest, line, configProblems, "display.lore-normal");
                            }
                            for (String line : displayItem.getLoreStarted()) {
                                findInvalidTaskReferences(quest, line, configProblems, "display.lore-started");
                            }

                            if (config.isConfigurationSection("placeholders")) {
                                for (String p : config.getConfigurationSection("placeholders").getKeys(false)) {
                                    placeholders.put(p, config.getString("placeholders." + p));
                                    findInvalidTaskReferences(quest, config.getString("placeholders." + p), configProblems, "placeholders." + p);
                                }
                            }
                            if (plugin.getConfig().getBoolean("options.show-quest-registrations")) {
                                plugin.getQuestsLogger().info("Registering quest " + quest.getId() + " with " + quest.getTasks().size() + " tasks.");
                            }
                            plugin.getQuestManager().registerQuest(quest);
                            plugin.getTaskTypeManager().registerQuestTasksWithTaskTypes(quest);
                            pathToQuest.put(relativeLocation.getPath(), quest);
                        }
                        if (!configProblems.isEmpty()) {
                            filesWithProblems.put(relativeLocation.getPath(), configProblems);
                        }
                    } catch (Exception e) {
                        plugin.getQuestsLogger().severe("An exception occurred when attempting to load quest '" + path + "' (will be ignored)");
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            };

            try {
                Files.walkFileTree(Paths.get(plugin.getDataFolder() + File.separator + "quests"), fileVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // post-load checks
            for (Map.Entry<String, Quest> loadedQuest : pathToQuest.entrySet()) {
                List<ConfigProblem> configProblems = new ArrayList<>();
                for (String req : loadedQuest.getValue().getRequirements()) {
                    if (plugin.getQuestManager().getQuestById(req) == null) {
                        configProblems.add(new ConfigProblem(ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_REQUIREMENT.getDescription(req), "options.requires"));
                    }
                }

                if (!configProblems.isEmpty()) {
                    if (filesWithProblems.containsKey(loadedQuest.getKey())) {
                        filesWithProblems.get(loadedQuest.getKey()).addAll(configProblems);
                    } else {
                        filesWithProblems.put(loadedQuest.getKey(), configProblems);
                    }
                }
            }

            for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                try {
                    taskType.onReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        problemsCount = 0;
        for (List<QuestsConfigLoader.ConfigProblem> problemList : plugin.getQuestsConfigLoader().getFilesWithProblems().values()) {
            problemsCount = problemsCount + problemList.size();
        }
    }

    public Map<String, List<ConfigProblem>> getFilesWithProblems() {
        return filesWithProblems;
    }

    public int getProblemsCount() {
        return problemsCount;
    }

    private void findInvalidTaskReferences(Quest quest, String s, List<ConfigProblem> configProblems, String location) {
        Pattern pattern = Pattern.compile("\\{([^}]+)}");

        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String[] parts = matcher.group(1).split(":");
            boolean match = false;
            for (Task t : quest.getTasks()) {
                if (t.getId().equals(parts[0])) {
                    match = true;
                    break;
                }
            }
            if (!match)
                configProblems.add(new ConfigProblem(ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_TASK_REFERENCE.getDescription(parts[0]), location));
        }
    }

    private QItemStack getQItemStack(String path, FileConfiguration config) {
        String cName = config.getString(path + ".name", path + ".name");
        List<String> cLoreNormal = config.getStringList(path + ".lore-normal");
        List<String> cLoreStarted = config.getStringList(path + ".lore-started");

        List<String> loreNormal = translateColoursInList(cLoreNormal);
        List<String> loreStarted = translateColoursInList(cLoreStarted);

        String name;
        name = ChatColor.translateAlternateColorCodes('&', cName);

        ItemStack is = plugin.getItemStack(path, config,
                ItemGetter.Filter.DISPLAY_NAME, ItemGetter.Filter.LORE, ItemGetter.Filter.ENCHANTMENTS, ItemGetter.Filter.ITEM_FLAGS);

        return new QItemStack(plugin, name, loreNormal, loreStarted, is);
    }

    private List<String> translateColoursInList(List<String> list) {
        List<String> coloured = new ArrayList<>();
        for (String s : list) {
            coloured.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return coloured;
    }

    public enum ConfigProblemDescriptions {

        MALFORMED_YAML("Malformed YAML file, cannot read config"),
        INVALID_QUEST_ID("ID '%s' is invalid, must be alphanumeric, unique and with no spaces"),
        NO_TASKS("Quest contains no valid tasks"),
        NO_TASK_TYPE("Task type not specified"),
        UNKNOWN_TASK_TYPE("Task type '%s' does not exist"),
        NO_DISPLAY_NAME("No name specified"),
        NO_DISPLAY_MATERIAL("No material specified"),
        UNKNOWN_MATERIAL("Material '%s' does not exist"),
        UNKNOWN_ENTITY_TYPE("Entity type '%s' does not exist"),
        TASK_MALFORMED_NOT_SECTION("Task '%s' is not a configuration section (has no fields)"),
        TASK_MISSING_FIELD("Required field '%s' is missing for task type '%s'"),
        UNKNOWN_TASK_REFERENCE("Attempt to reference unknown task '%s'"),
        UNKNOWN_CATEGORY("Category '%s' does not exist"),
        UNKNOWN_REQUIREMENT("Quest requirement '%s' does not exist");

        private final String description;

        ConfigProblemDescriptions(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return getDescription();
        }

        public String getDescription(String... format) {
            return String.format(description, (Object[]) format);
        }
    }

    public enum ConfigProblemType {

        ERROR("Error", "E", ChatColor.RED, 1),
        WARNING("Warning", "W", ChatColor.YELLOW, 2);

        private final String title;
        private final String shortened;
        private final ChatColor color;
        private final int priority;

        ConfigProblemType(String title, String shortened, ChatColor color, int priority) {
            this.title = title;
            this.shortened = shortened;
            this.color = color;
            this.priority = priority;
        }

        public String getTitle() {
            return title;
        }

        public String getShortened() {
            return shortened;
        }

        public ChatColor getColor() {
            return color;
        }

        public int getPriority() {
            return priority;
        }

    }

    public static class ConfigProblem {

        private final ConfigProblemType type;
        private final String description;
        private final String location;

        public ConfigProblem(ConfigProblemType type, String description, String location) {
            this.type = type;
            this.description = description == null ? "?" : description;
            ;
            this.location = location == null ? "?" : location;
        }

        public ConfigProblem(ConfigProblemType type, String description) {
            this.type = type;
            this.description = description == null ? "?" : description;
            ;
            this.location = "?";
        }

        public ConfigProblemType getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getLocation() {
            return location;
        }
    }
}
