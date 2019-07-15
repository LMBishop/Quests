package com.leonardobishop.quests;

import com.google.common.io.ByteStreams;
import com.leonardobishop.quests.bstats.Metrics;
import com.leonardobishop.quests.commands.CommandQuests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.events.EventPlayerJoin;
import com.leonardobishop.quests.events.EventPlayerLeave;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.QPlayerManager;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
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
import org.bukkit.configuration.file.FileConfiguration;
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
    private static Metrics metrics;
    private static Title title;
    private boolean brokenConfig = false;
    private QuestsConfigLoader questsConfigLoader;

    public static Quests get() {
        return instance;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public QPlayerManager getPlayerManager() {
        return qPlayerManager;
    }

    public TaskTypeManager getTaskTypeManager() {
        return taskTypeManager;
    }

    public boolean isBrokenConfig() {
        return brokenConfig;
    }

    public void setBrokenConfig(boolean brokenConfig) {
        this.brokenConfig = brokenConfig;
    }

    public Title getTitle() {
        return title;
    }

    public Updater getUpdater() {
        return updater;
    }

    public QuestsConfigLoader getQuestsConfigLoader() {
        return questsConfigLoader;
    }

    public String convertToFormat(long m) {
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


    @Override
    public void onEnable() {
        instance = this;
        taskTypeManager = new TaskTypeManager(this);
        questManager = new QuestManager(this);
        qPlayerManager = new QPlayerManager(this);
        QuestsAPI.initialise(this);

        dataGenerator();
        setupTitle();

        Bukkit.getPluginCommand("quests").setExecutor(new CommandQuests(this));
        Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new EventInventory(this), this);
        Bukkit.getPluginManager().registerEvents(new EventPlayerLeave(this), this);

        metrics = new Metrics(this);
        this.getLogger().log(Level.INFO, "Metrics started. This can be disabled at /plugins/bStats/config.yml.");

        questsConfigLoader = new QuestsConfigLoader(Quests.this);

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
                taskTypeManager.registerTaskType(new ExpEarnTaskType());
                taskTypeManager.registerTaskType(new BreedingTaskType());
                taskTypeManager.registerTaskType(new EnchantingTaskType());
                taskTypeManager.registerTaskType(new DealDamageTaskType());
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
                if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                    taskTypeManager.registerTaskType(new MythicMobsKillingType());
                }

                reloadQuests();
                if (!questsConfigLoader.getBrokenFiles().isEmpty()) {
                    Quests.this.getLogger().warning("Quests has failed to load the following files:");
                    for (Map.Entry<String, QuestsConfigLoader.ConfigLoadError> entry : questsConfigLoader.getBrokenFiles().entrySet()) {
                        Quests.this.getLogger().warning(" - " + entry.getKey() + ": " + entry.getValue().getMessage());
                    }
                }

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
                    for (Map.Entry<String, Quest> entry : Quests.this.getQuestManager().getQuests().entrySet()) {
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

        questsConfigLoader.loadConfig();
    }

    public ItemStack getItemStack(String path, FileConfiguration config) {
        String cName = config.getString(path + ".name", path + ".name");
        String cType = config.getString(path + ".type", path + ".type");
        List<String> cLore = config.getStringList(path + ".lore");

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
            }
        }

        File questsDirectory = new File(String.valueOf(this.getDataFolder() + File.separator + "quests"));
        if (!questsDirectory.exists() && !questsDirectory.isDirectory()) {
            questsDirectory.mkdir();

            ArrayList<String> examples = new ArrayList<>();
            examples.add("example1.yml");
            examples.add("example2.yml");
            examples.add("example3.yml");
            examples.add("example4.yml");
            examples.add("example5.yml");
            examples.add("example6.yml");
            examples.add("README.txt");

            for (String name : examples) {
                File file = new File(this.getDataFolder() + File.separator + "quests" + File.separator + name);
                try {
                    file.createNewFile();
                    try (InputStream in = Quests.class.getClassLoader().getResourceAsStream("quests/" + name)) {
                        OutputStream out = new FileOutputStream(file);
                        ByteStreams.copy(in, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
