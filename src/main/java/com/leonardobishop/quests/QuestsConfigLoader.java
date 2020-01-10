package com.leonardobishop.quests;

import com.leonardobishop.quests.obj.misc.QItemStack;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        for (String id : plugin.getConfig().getConfigurationSection("categories").getKeys(false)) {
            ItemStack displayItem = plugin.getItemStack("categories." + id + ".display", plugin.getConfig());
            boolean permissionRequired = plugin.getConfig().getBoolean("categories." + id + ".permission-required", false);

            Category category = new Category(id, displayItem, permissionRequired);
            plugin.getQuestManager().registerCategory(category);
        }

        File questDirectory = new File(plugin.getDataFolder() + File.separator + "quests");
        if (questDirectory.isDirectory()) {
            File[] fileList = questDirectory.listFiles();
            for (File questFile : fileList) {
                if (!questFile.getName().toLowerCase().endsWith(".yml")) continue;

                YamlConfiguration config = new YamlConfiguration();
                // test QUEST file integrity
                try {
                    config.load(questFile);
                } catch (Exception ex) {
                    brokenFiles.put(questFile.getName(), ConfigLoadError.MALFORMED_YAML);
                    continue;
                }

                String id = questFile.getName().replace(".yml", "");

                if (!StringUtils.isAlphanumeric(id)) {
                    brokenFiles.put(questFile.getName(), ConfigLoadError.INVALID_QUEST_ID);
                    continue;
                }

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
            }
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

        String name;
        Material type;
        int data = 0;
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

        type = Material.matchMaterial(cType);


        if (type == null) {
            type = Material.STONE;
        }

        return new QItemStack(name, loreNormal, loreStarted, type, data);
    }

    public enum ConfigLoadError {

        MALFORMED_YAML("Malformed YAML"),
        INVALID_QUEST_ID("Invalid quest ID (must be alphanumeric)");

        private String message;

        ConfigLoadError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
