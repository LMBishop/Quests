package com.leonardobishop.quests;

import com.google.common.io.ByteStreams;
import com.leonardobishop.quests.blocktype.SimilarBlocks;
import com.leonardobishop.quests.bstats.Metrics;
import com.leonardobishop.quests.commands.CommandQuests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.events.EventPlayerJoin;
import com.leonardobishop.quests.events.EventPlayerLeave;
import com.leonardobishop.quests.obj.misc.QItemStack;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.QPlayerManager;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.QuestManager;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import com.leonardobishop.quests.quests.tasktypes.types.*;
import com.leonardobishop.quests.title.Title;
import com.leonardobishop.quests.title.Title_Bukkit;
import com.leonardobishop.quests.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.title.Title_Other;
import com.leonardobishop.quests.updater.Updater;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Quests extends JavaPlugin {

    private static Quests instance;
    private static QuestManager questManager;
    private static QPlayerManager qPlayerManager;
    private static TaskTypeManager taskTypeManager;
    private static Updater updater;
    private static Title title;
    private boolean brokenConfig = false;

    public static Quests getInstance() {
        return instance;
    }

    public static QuestManager getQuestManager() {
        return questManager;
    }

    public static QPlayerManager getPlayerManager() {
        return qPlayerManager;
    }

    public static TaskTypeManager getTaskTypeManager() {
        return taskTypeManager;
    }

    public boolean isBrokenConfig() {
        return brokenConfig;
    }

    public static Title getTitle() {
        return title;
    }

    public static Updater getUpdater() {
        return updater;
    }

    public static String convertToFormat(long m) {
        long hours = m / 60;
        long minutesLeft = m - hours * 60;

        String formattedTime = "";

        if (hours < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + hours + "h";

        formattedTime = formattedTime + " ";

        if (minutesLeft < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + minutesLeft + "m";

        return formattedTime;
    }

    public Quests() {
    }

    public Quests(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public void prepareForTest() {
        instance = this;
        taskTypeManager = new TaskTypeManager();
        questManager = new QuestManager();
        qPlayerManager = new QPlayerManager();

        updater = new Updater(this);
    }


    @Override
    public void onEnable() {
        instance = this;
        taskTypeManager = new TaskTypeManager();
        questManager = new QuestManager();
        qPlayerManager = new QPlayerManager();

        dataGenerator();
        setupTitle();

        Bukkit.getPluginCommand("quests").setExecutor(new CommandQuests());
        Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new EventInventory(), this);
        Bukkit.getPluginManager().registerEvents(new EventPlayerLeave(), this);

        Metrics metrics = new Metrics(this);
        this.getLogger().log(Level.INFO, "Metrics started. This can be disabled at /plugins/bStats/config.yml.");

        SimilarBlocks.addBlocks();

        new BukkitRunnable() {
            @Override
            public void run() {
                taskTypeManager.registerTaskType(new MiningTaskType());
                taskTypeManager.registerTaskType(new MiningCertainTaskType());
                taskTypeManager.registerTaskType(new BuildingTaskType());
                taskTypeManager.registerTaskType(new BuildingCertainTaskType());
                taskTypeManager.registerTaskType(new MobkillingTaskType());
                taskTypeManager.registerTaskType(new MobkillingCertainTaskType());
                taskTypeManager.registerTaskType(new PlayerkillingTaskType());
                taskTypeManager.registerTaskType(new FishingTaskType());
                taskTypeManager.registerTaskType(new InventoryTaskType());
                taskTypeManager.registerTaskType(new WalkingTaskType());
                taskTypeManager.registerTaskType(new TamingTaskType());
                taskTypeManager.registerTaskType(new MilkingTaskType());
                taskTypeManager.registerTaskType(new ShearingTaskType());
                taskTypeManager.registerTaskType(new PositionTaskType());
                taskTypeManager.registerTaskType(new PlaytimeTaskType());
                taskTypeManager.registerTaskType(new BrewingTaskType());
                // TODO: FIX
                // taskTypeManager.registerTaskType(new BrewingCertainTaskType());
                if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock")) {
                    taskTypeManager.registerTaskType(new ASkyBlockLevelType());
                }
                if (Bukkit.getPluginManager().isPluginEnabled("uSkyBlock")) {
                    taskTypeManager.registerTaskType(new uSkyBlockLevelType());
                }
                if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                    taskTypeManager.registerTaskType(new CitizensDeliverTaskType());
                    taskTypeManager.registerTaskType(new CitizensInteractTaskType());
                }

                reloadQuests();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    qPlayerManager.loadPlayer(player.getUniqueId());
                }
            }
        }.runTask(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
                    if (qPlayer.isOnlyDataLoaded()) {
                        continue;
                    }
                    qPlayer.getQuestProgressFile().saveToDisk();
                }
            }
        }.runTaskTimerAsynchronously(this, 12000L, 12000L);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
                    if (qPlayer.isOnlyDataLoaded()) {
                        continue;
                    }
                    QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                    for (Map.Entry<String, Quest> entry : Quests.getQuestManager().getQuests().entrySet()) {
                        Quest quest = entry.getValue();
                        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
                        if (questProgressFile.hasStartedQuest(quest)) {
                            boolean complete = true;
                            for (Task task : quest.getTasks()) {
                                TaskProgress taskProgress;
                                if ((taskProgress = questProgress.getTaskProgress(task.getId())) == null || !taskProgress.isCompleted()) {
                                    complete = false;
                                    break;
                                }
                            }
                            if (complete) {
                                questProgressFile.completeQuest(quest);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
        new BukkitRunnable() {
            @Override
            public void run() {
                updater = new Updater(Quests.this);
                updater.check();
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
            if (qPlayer.isOnlyDataLoaded()) {
                continue;
            }
            qPlayer.getQuestProgressFile().saveToDisk();
        }
    }

    public void reloadQuests() {
        questManager.getQuests().clear();
        questManager.getCategories().clear();
        taskTypeManager.resetTaskTypes();

        // test file integrity
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(String.valueOf(Quests.this.getDataFolder() + File.separator + "config.yml")));
        } catch (Exception ex) {
            Quests.this.getLogger().log(Level.SEVERE, "You have a YAML error in your Quests config. If this is your first time using Quests, please remove the Quests folder and RESTART (not reload!) the server and try again.");
            brokenConfig = true;
        }

        for (String id : getConfig().getConfigurationSection("categories").getKeys(false)) {
            ItemStack displayItem = getItemStack("categories." + id + ".display");
            boolean permissionRequired = getConfig().getBoolean("categories." + id + ".permission-required", false);

            Category category = new Category(id, displayItem, permissionRequired);
            questManager.registerCategory(category);
        }

        for (String id : getConfig().getConfigurationSection("quests").getKeys(false)) {
            String root = "quests." + id;

            QItemStack displayItem = getQItemStack(root + ".display");
            List<String> rewards = getConfig().getStringList(root + ".rewards");
            List<String> requirements = getConfig().getStringList(root + ".options.requires");
            List<String> rewardString = getConfig().getStringList(root + ".rewardstring");
            boolean repeatable = getConfig().getBoolean(root + ".options.repeatable", false);
            boolean cooldown = getConfig().getBoolean(root + ".options.cooldown.enabled", false);
            boolean permissionRequired = getConfig().getBoolean(root + ".options.permission-required", false);
            int cooldownTime = getConfig().getInt(root + ".options.cooldown.time", 10);
            String category = getConfig().getString(root + ".options.category");

            if (rewardString == null) {
                rewardString = new ArrayList<>();
            }
            if (requirements == null) {
                requirements = new ArrayList<>();
            }
            if (rewards == null) {
                rewards = new ArrayList<>();
            }
            if (category == null) {
                category = "";
            }


            Quest quest;
            if (category.equals("")) {
                quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString);
            } else {
                quest = new Quest(id, displayItem, rewards, requirements, repeatable, cooldown, cooldownTime, permissionRequired, rewardString, category);
                Category c = questManager.getCategoryById(category);
                if (c != null) {
                    c.registerQuestId(id);
                }
            }

            for (String taskId : getConfig().getConfigurationSection(root + ".tasks").getKeys(false)) {
                String taskRoot = root + ".tasks." + taskId;
                String taskType = getConfig().getString(taskRoot + ".type");

                Task task = new Task(taskId, taskType);

                for (String key : getConfig().getConfigurationSection(taskRoot).getKeys(false)) {
                    task.addConfigValue(key, getConfig().get(taskRoot + "." + key));
                }

                quest.registerTask(task);
            }

            if (getConfig().getBoolean("options.show-quest-registrations")) {
                this.getLogger().log(Level.INFO, "Registering quest " + quest.getId() + " with " + quest.getTasks().size() + " tasks.");
            }
            questManager.registerQuest(quest);
            taskTypeManager.registerQuestTasksWithTaskTypes(quest);
        }
    }

    private QItemStack getQItemStack(String path) {
        String cName = this.getConfig().getString(path + ".name", path + ".name");
        String cType = this.getConfig().getString(path + ".type", path + ".type");
        List<String> cLoreNormal = this.getConfig().getStringList(path + ".lore-normal");
        List<String> cLoreStarted = this.getConfig().getStringList(path + ".lore-started");

        String name;
        Material type = null;
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

        if (StringUtils.isNumeric(cType)) {
            type = Material.getMaterial(Integer.parseInt(cType));
        } else if (Material.getMaterial(cType) != null) {
            type = Material.getMaterial(cType);
        } else if (cType.contains(":")) {
            String[] parts = cType.split(":");
            if (parts.length > 1) {
                if (StringUtils.isNumeric(parts[0])) {
                    type = Material.getMaterial(Integer.parseInt(parts[0]));
                } else if (Material.getMaterial(parts[0]) != null) {
                    type = Material.getMaterial(parts[0]);
                }
                if (StringUtils.isNumeric(parts[1])) {
                    data = Integer.parseInt(parts[1]);
                }
            }
        }

        if (type == null) {
            type = Material.STONE;
        }

        QItemStack is = new QItemStack(name, loreNormal, loreStarted, type, data);

        return is;
    }

    public ItemStack getItemStack(String path) {
        String cName = this.getConfig().getString(path + ".name", path + ".name");
        String cType = this.getConfig().getString(path + ".type", path + ".type");
        List<String> cLore = this.getConfig().getStringList(path + ".lore");

        String name;
        Material type = null;
        int data = 0;
        List<String> lore = new ArrayList<>();
        if (cLore != null) {
            for (String s : cLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        name = ChatColor.translateAlternateColorCodes('&', cName);

        if (StringUtils.isNumeric(cType)) {
            type = Material.getMaterial(Integer.parseInt(cType));
        } else if (Material.getMaterial(cType) != null) {
            type = Material.getMaterial(cType);
        } else if (cType.contains(":")) {
            String[] parts = cType.split(":");
            if (parts.length > 1) {
                if (StringUtils.isNumeric(parts[0])) {
                    type = Material.getMaterial(Integer.parseInt(parts[0]));
                } else if (Material.getMaterial(parts[0]) != null) {
                    type = Material.getMaterial(parts[0]);
                }
                if (StringUtils.isNumeric(parts[1])) {
                    data = Integer.parseInt(parts[1]);
                }
            }
        }

        if (type == null) {
            type = Material.STONE;
        }

        ItemStack is = new ItemStack(type, 1, (short) data);
        ItemMeta ism = is.getItemMeta();
        ism.setLore(lore);
        ism.setDisplayName(name);
        is.setItemMeta(ism);

        return is;
    }

    private boolean setupTitle() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        boolean success = false;
        getLogger().info("Your server is running version " + version + ".");
        if (version.startsWith("v1_7")) {
            title = new Title_Other();
            success = true;
        } else if (version.equals("v1_8_R3")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_8_R2")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_8_R1")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_9_R2")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_9_R1")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_10_R1")) {
            title = new Title_BukkitNoTimings();
            success = true;
        } else if (version.equals("v1_11_R1")) {
            title = new Title_Bukkit();
            success = true;
        } else if (version.startsWith("v1_12")) {
            title = new Title_Bukkit();
            success = true;
        } else if (version.startsWith("v1_13")) {
            title = new Title_Bukkit();
            success = true;
        } else if (version.startsWith("v1_14")) {
            title = new Title_Bukkit();
            success = true;
        } else {
            title = new Title_BukkitNoTimings();
        }
        if (title instanceof Title_Bukkit) {
            getLogger().info("Titles have been enabled.");
        } else if (title instanceof Title_BukkitNoTimings) {
            getLogger().info("Titles have been enabled, although they have limited timings.");
        } else {
            getLogger().info("Titles are not supported for this version.");
        }
        return success;
    }

    private void dataGenerator() {
        File directory = new File(String.valueOf(this.getDataFolder()));
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            try {
                config.createNewFile();
                try (InputStream in = Quests.class.getClassLoader().getResourceAsStream("config.yml")) {
                    OutputStream out = new FileOutputStream(config);
                    ByteStreams.copy(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
