package com.leonardobishop.quests;

import com.leonardobishop.quests.itemgetter.ItemGetter;
import com.leonardobishop.quests.obj.misc.QItemStack;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestsConfigLoader {

    private final Map<String, ConfigLoadError> brokenFiles = new HashMap<>();
    private final Quests plugin;

    public QuestsConfigLoader(Quests plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads and parses config into memory including the quests folder.
     */
    public void loadConfig() {
        plugin.reloadConfig();
        brokenFiles.clear();
        plugin.setBrokenConfig(false);

        // test CONFIG file integrity
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        } catch (Exception ex) {
            brokenFiles.put("<MAIN CONFIG> config.yml", new ConfigLoadError(ConfigLoadErrorType.MALFORMED_YAML));
            plugin.setBrokenConfig(true);
            return;
        }

        for (String id : plugin.getConfig().getConfigurationSection("categories").getKeys(false)) {
            ItemStack displayItem = plugin.getItemStack("categories." + id + ".display", plugin.getConfig());
            boolean permissionRequired = plugin.getConfig().getBoolean("categories." + id + ".permission-required", false);

            Category category = new Category(id, displayItem, permissionRequired);
            plugin.getQuestManager().registerCategory(category);
        }
        plugin.getQuestsLogger().setServerLoggingLevel(LoggingLevel.fromNumber(plugin.getConfig().getInt("options.verbose-logging-level", 2)));

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            final URI questsRoot = Paths.get(plugin.getDataFolder() + File.separator + "quests").toUri();

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                File questFile = new File(path.toUri());
                URI relativeLocation = questsRoot.relativize(path.toUri());

                if (!questFile.getName().toLowerCase().endsWith(".yml")) return FileVisitResult.CONTINUE;

                YamlConfiguration config = new YamlConfiguration();
                // test QUEST file integrity
                try {
                    config.load(questFile);
                } catch (Exception ex) {
                    brokenFiles.put(relativeLocation.getPath(), new ConfigLoadError(ConfigLoadErrorType.MALFORMED_YAML));
                    return FileVisitResult.CONTINUE;
                }

                String id = questFile.getName().replace(".yml", "");

                if (!StringUtils.isAlphanumeric(id)) {
                    brokenFiles.put(relativeLocation.getPath(), new ConfigLoadError(ConfigLoadErrorType.INVALID_QUEST_ID));
                    return FileVisitResult.CONTINUE;
                }

                // CHECK EVERYTHING WRONG WITH THE QUEST FILE BEFORE ACTUALLY LOADING THE QUEST

                List<String> questErrors = new ArrayList<>();
                List<String> taskErrors = new ArrayList<>();

                if (!config.isConfigurationSection("tasks")) {
                    questErrors.add("'tasks' section not defined");
                } else { //continue
                    for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                        String taskRoot = "tasks." + taskId;
                        String taskType = config.getString(taskRoot + ".type");

                        if (!config.isConfigurationSection(taskRoot)) {
                            questErrors.add("task '" + taskId + "' cannot be read (has no children)");
                            continue;
                        }

                        // check the tasks
                        TaskType t = plugin.getTaskTypeManager().getTaskType(taskType);
                        if (t != null) {
                            List<String> missingFields = new ArrayList<>();
                            for (ConfigValue cv : t.getCreatorConfigValues()) {
                                if (cv.isRequired() && config.get(taskRoot + "." + cv.getKey()) == null)
                                    missingFields.add(cv.getKey());
                            }
                            if (!missingFields.isEmpty())
                                taskErrors.add("task '" + taskId + "': '" + t.getType() + "' missing required field(s) '" + String.join(", ", missingFields) + "'");
                        }
                    }
                }

                if (!questErrors.isEmpty()) { //if the file quest is not okay, do not load the quest
                    brokenFiles.put(relativeLocation.getPath(), new ConfigLoadError(ConfigLoadErrorType.MALFORMED_QUEST, String.join("; ", questErrors)));
                    return FileVisitResult.CONTINUE; //next quest please!
                } else if (!taskErrors.isEmpty()) { // likewise with tasks
                    brokenFiles.put(relativeLocation.getPath(), new ConfigLoadError(ConfigLoadErrorType.MALFORMED_TASK, String.join("; ", taskErrors)));
                    return FileVisitResult.CONTINUE;
                }

                // END OF THE CHECKING

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

                if (category == null) category = "";

                Quest quest;
                if (category.equals("")) {
                    quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString, startString, sortOrder);
                } else {
                    quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString, startString, category, sortOrder);
                    Category c = plugin.getQuestManager().getCategoryById(category);
                    if (c != null) {
                        c.registerQuestId(id);
                    }
                }

                for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                    String taskRoot = "tasks." + taskId;
                    String taskType = config.getString(taskRoot + ".type");

                    Task task = new Task(taskId, taskType);

                    for (String key : config.getConfigurationSection(taskRoot).getKeys(false)) {
                        task.addConfigValue(key, config.get(taskRoot + "." + key));
                    }

                    quest.registerTask(task);
                }

                if (plugin.getConfig().getBoolean("options.show-quest-registrations")) {
                    plugin.getQuestsLogger().info("Registering quest " + quest.getId() + " with " + quest.getTasks().size() + " tasks.");
                }
                plugin.getQuestManager().registerQuest(quest);
                plugin.getTaskTypeManager().registerQuestTasksWithTaskTypes(quest);
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(Paths.get(plugin.getDataFolder() + File.separator + "quests"), fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
            try {
                taskType.onReady();
            } catch (Exception ignored) { }
        }
    }

    /**
     * Gets recent file errors during load.
     *
     * @return Errors during load, Map<String, ConfigLoadError> of file name and error
     */
    public Map<String, ConfigLoadError> getBrokenFiles() {
        return brokenFiles;
    }

    private QItemStack getQItemStack(String path, FileConfiguration config) {
        String cName = config.getString(path + ".name", path + ".name");
        List<String> cLoreNormal = config.getStringList(path + ".lore-normal");
        List<String> cLoreStarted = config.getStringList(path + ".lore-started");

        String name;
        List<String> loreNormal = new ArrayList<>();
        if (cLoreNormal != null) {
            for (String s : cLoreNormal) {
                loreNormal.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        List<String> loreStarted = new ArrayList<>();
        if (cLoreStarted != null) {
            for (String s : cLoreStarted) {
                loreStarted.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        name = ChatColor.translateAlternateColorCodes('&', cName);

        ItemStack is = plugin.getItemStack(path, config,
                ItemGetter.Filter.DISPLAY_NAME, ItemGetter.Filter.LORE, ItemGetter.Filter.ENCHANTMENTS, ItemGetter.Filter.ITEM_FLAGS);

        return new QItemStack(name, loreNormal, loreStarted, is);
    }

    public enum ConfigLoadErrorType {

        MALFORMED_YAML("Malformed YAML"),
        INVALID_QUEST_ID("Invalid quest ID (must be alphanumeric)"),
        MALFORMED_QUEST("Quest file is not configured properly: %s"),
        MALFORMED_TASK("Tasks are not configured properly: %s");

        private String message;

        ConfigLoadErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    public class ConfigLoadError {

        private ConfigLoadErrorType type;
        private String[] extraInfo;

        public ConfigLoadError(ConfigLoadErrorType type, String... extraInfo) {
            this.type = type;
            this.extraInfo = extraInfo;
        }

        public String getMessage() {
            return String.format(type.getMessage(), (Object[]) extraInfo);
        }
    }
}
