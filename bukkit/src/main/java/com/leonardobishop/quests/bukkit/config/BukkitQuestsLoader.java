package com.leonardobishop.quests.bukkit.config;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStackRegistry;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.config.QuestsLoader;
import com.leonardobishop.quests.common.logger.QuestsLogger;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.QuestManager;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.apache.commons.lang.StringUtils;
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
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BukkitQuestsLoader implements QuestsLoader {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig questsConfig;
    private final QuestManager questManager;
    private final TaskTypeManager taskTypeManager;
    private final QuestController questController;
    private final QuestsLogger questsLogger;
    private final QItemStackRegistry qItemStackRegistry;

    public BukkitQuestsLoader(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.questsConfig = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.questManager = plugin.getQuestManager();
        this.taskTypeManager = plugin.getTaskTypeManager();
        this.questController = plugin.getQuestController();
        this.questsLogger = plugin.getQuestsLogger();
        this.qItemStackRegistry = plugin.getQItemStackRegistry();
    }

    @Override
    public Map<String, List<ConfigProblem>> loadQuests(File root) {
        qItemStackRegistry.clearRegistry();
        questManager.clear();
        taskTypeManager.resetTaskTypes();

        Map<String, List<ConfigProblem>> configProblems = new HashMap<>();
        HashMap<String, Quest> pathToQuest = new HashMap<>();
        HashMap<String, Map<String, Object>> globalTaskConfig = new HashMap<>();

        if (questsConfig.getConfig().isConfigurationSection("global-task-configuration.types")) {
            for (String type : questsConfig.getConfig().getConfigurationSection("global-task-configuration.types").getKeys(false)) {
                HashMap<String, Object> configValues = new HashMap<>();
                for (String key : questsConfig.getConfig().getConfigurationSection("global-task-configuration.types." + type).getKeys(false)) {
                    configValues.put(key, questsConfig.getConfig().get("global-task-configuration.types." + type + "." + key));
                }
                globalTaskConfig.putIfAbsent(type, configValues);
            }
        }

        for (String id : plugin.getConfig().getConfigurationSection("categories").getKeys(false)) {
            ItemStack displayItem = plugin.getItemStack("categories." + id + ".display", plugin.getConfig());
            boolean permissionRequired = plugin.getConfig().getBoolean("categories." + id + ".permission-required", false);

            Category category = new Category(id, permissionRequired);
            questManager.registerCategory(category);
            qItemStackRegistry.register(category, displayItem);
        }

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                try {
                    File questFile = new File(path.toUri());
                    URI relativeLocation = root.toURI().relativize(path.toUri());

                    if (!questFile.getName().toLowerCase().endsWith(".yml")) return FileVisitResult.CONTINUE;

                    YamlConfiguration config = new YamlConfiguration();
                    // test QUEST file integrity
                    try {
                        config.load(questFile);
                    } catch (Exception ex) {
                        configProblems.put(relativeLocation.getPath(), Collections.singletonList(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.MALFORMED_YAML.getDescription())));
                        return FileVisitResult.CONTINUE;
                    }

                    String id = questFile.getName().replace(".yml", "");

                    List<ConfigProblem> problems = new ArrayList<>();

                    if (!StringUtils.isAlphanumeric(id)) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.INVALID_QUEST_ID.getDescription(id)));
                    }

                    // CHECK EVERYTHING WRONG WITH THE QUEST FILE BEFORE ACTUALLY LOADING THE QUEST

                    if (!config.isConfigurationSection("tasks")) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.NO_TASKS.getDescription(), "tasks"));
                    } else { //continue
                        int validTasks = 0;
                        for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                            boolean isValid = true;
                            String taskRoot = "tasks." + taskId;
                            String taskType = config.getString(taskRoot + ".type");

                            if (!config.isConfigurationSection(taskRoot)) {
                                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING, ConfigProblemDescriptions.TASK_MALFORMED_NOT_SECTION.getDescription(taskId), taskRoot));
                                continue;
                            }

                            if (taskType == null) {
                                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING, ConfigProblemDescriptions.NO_TASK_TYPE.getDescription(), taskRoot));
                                continue;
                            }

                            // check the tasks
                            TaskType t = taskTypeManager.getTaskType(taskType);
                            if (t != null) {
                                HashMap<String, Object> configValues = new HashMap<>();
                                for (String key : config.getConfigurationSection(taskRoot).getKeys(false)) {
                                    configValues.put(key, config.get(taskRoot + "." + key));
                                }

                                problems.addAll(t.validateConfig(taskRoot, configValues));
                            } else {
                                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_TASK_TYPE.getDescription(taskType), taskRoot));
                                isValid = false;
                            }

                            if (isValid) {
                                validTasks++;
                            }
                        }
                        if (validTasks == 0) {
                            problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.NO_TASKS.getDescription(), "tasks"));
                        }
                    }

                    boolean error = false;
                    for (ConfigProblem problem : problems) {
                        if (problem.getType() == ConfigProblem.ConfigProblemType.ERROR) {
                            error = true;
                            break;
                        }
                    }

                    // END OF THE CHECKING
                    if (!error && !questsConfig.getBoolean("options.error-checking.override-errors", false)) {
                        QItemStack displayItem = getQItemStack("display", config);
                        List<String> rewards = config.getStringList("rewards");
                        List<String> requirements = config.getStringList("options.requires");
                        List<String> rewardString = config.getStringList("rewardstring");
                        List<String> startString = config.getStringList("startstring");
                        boolean repeatable = config.getBoolean("options.repeatable", false);
                        boolean cooldown = config.getBoolean("options.cooldown.enabled", false);
                        boolean permissionRequired = config.getBoolean("options.permission-required", false);
                        boolean autostart = config.getBoolean("options.autostart", false);
                        int cooldownTime = config.getInt("options.cooldown.time", 10);
                        int sortOrder = config.getInt("options.sort-order", 1);
                        String category = config.getString("options.category");
                        Map<String, String> placeholders = new HashMap<>();

                        if (category != null && category.equals("")) category = null;

                        if (questController.getName().equals("daily")) {
                            repeatable = true;
                            cooldown = true;
                            cooldownTime = 0;
                            requirements = Collections.emptyList();
                            permissionRequired = false;
                        }

                        Quest quest = new Quest.Builder(id)
                                .withRewards(rewards)
                                .withRequirements(requirements)
                                .withRewardString(rewardString)
                                .withStartString(startString)
                                .withPlaceholders(placeholders)
                                .withCooldown(cooldownTime)
                                .withSortOrder(sortOrder)
                                .withCooldownEnabled(cooldown)
                                .withPermissionRequired(permissionRequired)
                                .withRepeatEnabled(repeatable)
                                .withAutoStartEnabled(autostart)
                                .inCategory(category)
                                .build();

                        if (category != null) {
                            Category c = questManager.getCategoryById(category);
                            if (c != null) {
                                c.registerQuestId(id);
                            } else {
                                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_CATEGORY.getDescription(category), "options.category"));
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
                                    if (questsConfig.getBoolean("options.global-task-configuration-override") && task.getConfigValue(entry.getKey()) != null)
                                        continue;
                                    task.addConfigValue(entry.getKey(), entry.getValue());
                                }
                            }

                            quest.registerTask(task);
                        }


                        for (String line : displayItem.getLoreNormal()) {
                            findInvalidTaskReferences(quest, line, problems, "display.lore-normal");
                        }
                        for (String line : displayItem.getLoreStarted()) {
                            findInvalidTaskReferences(quest, line, problems, "display.lore-started");
                        }

                        if (config.isConfigurationSection("placeholders")) {
                            for (String p : config.getConfigurationSection("placeholders").getKeys(false)) {
                                placeholders.put(p, config.getString("placeholders." + p));
                                findInvalidTaskReferences(quest, config.getString("placeholders." + p), problems, "placeholders." + p);
                            }
                        }
                        questManager.registerQuest(quest);
                        taskTypeManager.registerQuestTasksWithTaskTypes(quest);
                        qItemStackRegistry.register(quest, displayItem);
                        pathToQuest.put(relativeLocation.getPath(), quest);
                    }
                    if (!problems.isEmpty()) {
                        configProblems.put(relativeLocation.getPath(), problems);
                    }
                } catch (Exception e) {
                    questsLogger.severe("An exception occurred when attempting to load quest '" + path + "' (will be ignored)");
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(root.toPath(), fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        questsLogger.info(questManager.getQuests().size() + " quests have been registered.");

        // post-load checks
        for (Map.Entry<String, Quest> loadedQuest : pathToQuest.entrySet()) {
            List<ConfigProblem> problems = new ArrayList<>();
            for (String req : loadedQuest.getValue().getRequirements()) {
                if (questManager.getQuestById(req) == null) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING, ConfigProblemDescriptions.UNKNOWN_REQUIREMENT.getDescription(req), "options.requires"));
                }
            }

            if (!problems.isEmpty()) {
                if (configProblems.containsKey(loadedQuest.getKey())) {
                    configProblems.get(loadedQuest.getKey()).addAll(problems);
                } else {
                    configProblems.put(loadedQuest.getKey(), problems);
                }
            }
        }

        return configProblems;
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
                configProblems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_TASK_REFERENCE.getDescription(parts[0]), location));
        }
    }

    private QItemStack getQItemStack(String path, FileConfiguration config) {
        String cName = config.getString(path + ".name", path + ".name");
        List<String> cLoreNormal = config.getStringList(path + ".lore-normal");
        List<String> cLoreStarted = config.getStringList(path + ".lore-started");

        List<String> loreNormal = Chat.color(cLoreNormal);
        List<String> loreStarted = Chat.color(cLoreStarted);

        String name;
        name = Chat.color(cName);

        ItemStack is = plugin.getItemStack(path, config,
                ItemGetter.Filter.DISPLAY_NAME, ItemGetter.Filter.LORE, ItemGetter.Filter.ENCHANTMENTS, ItemGetter.Filter.ITEM_FLAGS);

        return new QItemStack(plugin, name, loreNormal, loreStarted, is);
    }

}
