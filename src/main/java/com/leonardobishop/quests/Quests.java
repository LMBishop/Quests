package com.leonardobishop.quests;

import com.leonardobishop.quests.api.QuestsPlaceholders;
import com.leonardobishop.quests.bstats.Metrics;
import com.leonardobishop.quests.commands.CommandQuests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.events.EventPlayerJoin;
import com.leonardobishop.quests.events.EventPlayerLeave;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.QPlayerManager;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.QuestManager;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import com.leonardobishop.quests.quests.tasktypes.types.*;
import com.leonardobishop.quests.sql.SQLConnector;
import com.leonardobishop.quests.title.Title;
import com.leonardobishop.quests.title.Title_Bukkit;
import com.leonardobishop.quests.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.title.Title_Other;
import com.leonardobishop.quests.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Quests extends JavaPlugin {
    private static SQLConnector connector;
    private static QuestManager questManager;
    private static QPlayerManager qPlayerManager;
    private static TaskTypeManager taskTypeManager;
    private static Updater updater;
    private static Title title;
    private boolean brokenConfig = false;
    private static QuestsConfigLoader questsConfigLoader;

    public SQLConnector getDatabase() {
        return connector;
    }

    public static Quests get() {
        return (Quests) Bukkit.getPluginManager().getPlugin("Quests");
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

    public String convertToFormat(long m) { //seconds please
        long hours = m / 3600;
        long minutes = (m % 3600) / 60;
        long seconds = ((m % 3600) % 60) % 60;

        return Messages.TIME_FORMAT.getMessage()
                .replace("{hours}", String.format("%02d", hours))
                .replace("{minutes}", String.format("%02d", minutes))
                .replace("{seconds}", String.format("%02d", seconds));
    }

    @Override
    public void onEnable() {
        taskTypeManager = new TaskTypeManager(this);
        questManager = new QuestManager(this);
        qPlayerManager = new QPlayerManager(this);
        connector = new SQLConnector(this);

        dataGenerator();
        setupTitle();

        Bukkit.getPluginCommand("quests").setExecutor(new CommandQuests(this));
        Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new EventInventory(this), this);
        Bukkit.getPluginManager().registerEvents(new EventPlayerLeave(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new QuestsPlaceholders(this).register();
        }

        Metrics metrics = new Metrics(this);
        if (metrics.isEnabled()) {
            this.getLogger().log(Level.INFO, "Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        questsConfigLoader = new QuestsConfigLoader(this);

        Bukkit.getScheduler().runTask(this, () -> {
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
            if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
                BentoBoxLevelTaskType.register(taskTypeManager);
            }
            if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock")) {
                taskTypeManager.registerTaskType(new IridiumSkyblockValueType());
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
                this.getLogger().warning("Quests has failed to load the following files:");
                for (Map.Entry<String, QuestsConfigLoader.ConfigLoadError> entry : questsConfigLoader.getBrokenFiles().entrySet()) {
                    this.getLogger().warning(" - " + entry.getKey() + ": " + entry.getValue().getMessage());
                }
                this.getLogger().warning(ChatColor.GRAY.toString() + ChatColor.ITALIC + "If this is your first time using Quests, please delete the Quests folder and RESTART (not reload!) the server.");
            }

            Bukkit.getOnlinePlayers().forEach(p -> qPlayerManager.loadPlayer(p.getUniqueId(), false, getDatabase().getStoreType()));
        });
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> qPlayerManager.getQPlayers().forEach(qPlayer -> {
            if (!qPlayer.isOnlyDataLoaded()) {
                qPlayer.getQuestProgressFile().saveToDisk(Quests.get().getDatabase().getStoreType());
            }
        }), 12000L, 12000L);
        Bukkit.getScheduler().runTaskTimer(this, () -> qPlayerManager.getQPlayers().forEach(qPlayer -> {
            if (!qPlayer.isOnlyDataLoaded()) {
                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                this.getQuestManager().getQuests().forEach((key, quest) -> {
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
                });
            }
        }), 10 * 20L, 10 * 20L); //Data is saved every 10 seconds in case of crash; the player data is also saved when the player leaves the server
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            updater = new Updater(this);
            updater.check();
        });
    }

    @Override
    public void onDisable() {
        for (TaskType taskType : getTaskTypeManager().getTaskTypes()) {
            try {
                taskType.onDisable();
            } catch (Exception ignored) {
            }
        }
        for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
            if (qPlayer.isOnlyDataLoaded()) {
                continue;
            }
            qPlayer.getQuestProgressFile().saveToDisk(Quests.get().getDatabase().getStoreType());
        }
    }

    public void reloadQuests() {
        questManager.getQuests().clear();
        questManager.getCategories().clear();
        taskTypeManager.resetTaskTypes();
        connector.stopConnection();

        questsConfigLoader.loadConfig();
    }

    public ItemStack getItemStack(String path, FileConfiguration config) {
        return getItemStack(config.getConfigurationSection(path));
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemStack(ConfigurationSection config) {
        String cName = config.getString("name", "Item Name");
        String cType = config.getString("type", "BEDROCK"); // Error, type bedrock, no more errors!!! :)
        int data = config.getInt("data", 0);
        List<String> cLore = config.getStringList("lore");

        String name;
        Material type;
        List<String> lore = new ArrayList<>();
        if (cLore != null && !cLore.isEmpty()) {
            for (String s : cLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        name = ChatColor.translateAlternateColorCodes('&', cName);
        type = Material.matchMaterial(cType);


        if (type == null) {
            type = Material.STONE;
        }

        ItemStack is;
        if (data == 0)
            is = new ItemStack(type, 1);
        else
            is = new ItemStack(type, 1, (short) data);
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
        } else if (version.startsWith("v1_8") || version.startsWith("v1_9") || version.startsWith("v1_10")) {
            title = new Title_BukkitNoTimings();
        } else {
            title = new Title_Bukkit();
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
                //try (InputStream in = Quests.class.getClassLoader().getResourceAsStream("config.yml")) {
                try (InputStream in = this.getResource("config.yml")) {
                    OutputStream out = new FileOutputStream(config);
                    byte[] buffer = new byte[1024];
                    int lenght = in.read(buffer);
                    while (lenght != -1) {
                        out.write(buffer, 0, lenght);
                        lenght = in.read(buffer);
                    }
                    //ByteStreams.copy(in, out); BETA method, data losses ahead
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File questsDirectory = new File(this.getDataFolder() + File.separator + "quests");
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
                    //try (InputStream in = Quests.class.getClassLoader().getResourceAsStream("quests/" + name)) {
                    try (InputStream in = this.getResource("quests/" + name)) {
                        OutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int lenght = in.read(buffer);
                        while (lenght != -1) {
                            out.write(buffer, 0, lenght);
                            lenght = in.read(buffer);
                        }
                        //ByteStreams.copy(in, out); BETA method, data losses ahead
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
