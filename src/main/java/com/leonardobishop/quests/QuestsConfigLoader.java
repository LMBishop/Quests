package com.leonardobishop.quests;

import com.leonardobishop.quests.api.enums.StoreType;
import com.leonardobishop.quests.obj.misc.QItemStack;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;

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
        brokenFiles.clear();

        // test CONFIG file integrity
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        } catch (Exception ex) {
            brokenFiles.put("<MAIN CONFIG> config.yml", ConfigLoadError.MALFORMED_YAML);
            plugin.setBrokenConfig(true);
            return;
        }

        String storeTypeData = plugin.getConfig().getString("database.type", "YAML");
        StoreType storeType;
        try {
            storeType = StoreType.valueOf(storeTypeData.toUpperCase());
        } catch (Exception e) {
            storeType = StoreType.YAML;
        }
        plugin.getDatabase().setStoreType(storeType);

        if (storeType == StoreType.SQL) {
            String fileName = plugin.getConfig().getString("database.file_name", "quests.db");
            plugin.getDatabase().loadSQL(fileName);
        } else if (storeType == StoreType.MY_SQL) {
            String host = plugin.getConfig().getString("database.host");
            String user = plugin.getConfig().getString("database.user");
            String pass = plugin.getConfig().getString("database.pass");
            String port = plugin.getConfig().getString("database.port");
            String name = plugin.getConfig().getString("database.name");
            String tablePrefix = plugin.getConfig().getString("database.table_prefix");
            boolean useSSL = plugin.getConfig().getBoolean("database.ssl");
            plugin.getDatabase().loadMySQL(host, user, pass, port, name, useSSL, tablePrefix);
        }

        for (String id : plugin.getConfig().getConfigurationSection("categories").getKeys(false)) {
            ItemStack displayItem = plugin.getItemStack("categories." + id + ".display", plugin.getConfig());
            boolean permissionRequired = plugin.getConfig().getBoolean("categories." + id + ".permission-required", false);

            Category category = new Category(id, displayItem, permissionRequired);
            plugin.getQuestManager().registerCategory(category);
        }

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
                    brokenFiles.put(relativeLocation.getPath(), ConfigLoadError.MALFORMED_YAML);
                    return FileVisitResult.CONTINUE;
                }

                String id = questFile.getName().replace(".yml", "");

                if (!StringUtils.isAlphanumeric(id)) {
                    brokenFiles.put(relativeLocation.getPath(), ConfigLoadError.INVALID_QUEST_ID);
                    return FileVisitResult.CONTINUE;
                }

                // CHECK EVERYTHING WRONG WITH THE QUEST FILE BEFORE ACTUALLY LOADING THE QUEST

                boolean isTheQuestFileOkay = true;

                if (!config.isConfigurationSection("tasks")) {
                    isTheQuestFileOkay = false;
                } else { //continue
                    for (String taskId : config.getConfigurationSection("tasks").getKeys(false)) {
                        String taskRoot = "tasks." + taskId;
                        String taskType = config.getString(taskRoot + ".type");

                        if (!config.isConfigurationSection(taskRoot)) {
                            isTheQuestFileOkay = false;
                            break; //do not loop if section do not exist, just break directly
                        }
                    }
                }

                if (!isTheQuestFileOkay) { //if the file quest is not okay, do not load the quest
                    brokenFiles.put(relativeLocation.getPath(), ConfigLoadError.MALFORMED_QUEST);
                    return FileVisitResult.CONTINUE; //next quest please!
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
                    plugin.getLogger().log(Level.INFO, "Registering quest " + quest.getId() + " with " + quest.getTasks().size() + " tasks.");
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
        String cType = config.getString(path + ".type", path + ".type");
        List<String> cLoreNormal = config.getStringList(path + ".lore-normal");
        List<String> cLoreStarted = config.getStringList(path + ".lore-started");
        int data = config.getInt(path + ".data", 0);

        String name;
        Material type;
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

        type = Material.getMaterial(cType);


        if (type == null) {
            type = Material.STONE;
        }

        return new QItemStack(name, loreNormal, loreStarted, type, data);
    }

    public enum ConfigLoadError {

        MALFORMED_YAML("Malformed YAML"),
        INVALID_QUEST_ID("Invalid quest ID (must be alphanumeric)"),
        MALFORMED_QUEST("Quest file isn't configured properly.");

        private String message;

        ConfigLoadError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
