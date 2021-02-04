package com.leonardobishop.quests;

import com.leonardobishop.quests.api.QuestsPlaceholders;
import com.leonardobishop.quests.bstats.Metrics;
import com.leonardobishop.quests.commands.CommandQuests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.events.EventPlayerJoin;
import com.leonardobishop.quests.events.EventPlayerLeave;
import com.leonardobishop.quests.itemgetter.ItemGetter;
import com.leonardobishop.quests.itemgetter.ItemGetterLatest;
import com.leonardobishop.quests.itemgetter.ItemGetter_1_13;
import com.leonardobishop.quests.itemgetter.ItemGetter_Late_1_8;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.QPlayerManager;
import com.leonardobishop.quests.quests.QuestManager;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import com.leonardobishop.quests.quests.tasktypes.types.*;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.*;
import com.leonardobishop.quests.title.Title;
import com.leonardobishop.quests.title.Title_Bukkit;
import com.leonardobishop.quests.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.title.Title_Other;
import com.leonardobishop.quests.updater.Updater;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.ArrayList;

public class Quests extends JavaPlugin {

    private static QuestManager questManager;
    private static QPlayerManager qPlayerManager;
    private static TaskTypeManager taskTypeManager;

    private static Updater updater;
    private static Title title;
    private ItemGetter itemGetter;
    private QuestCompleter questCompleter;
    private QuestsConfigLoader questsConfigLoader;
    private QuestsLogger questsLogger;
    private PlaceholderExpansion placeholder;

    private boolean brokenConfig = false;
    private BukkitTask questCompleterTask;
    private BukkitTask questAutosaveTask;

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
        questsLogger = new QuestsLogger(this, QuestsLogger.LoggingLevel.INFO);
        questCompleter = new QuestCompleter(this);

        taskTypeManager = new TaskTypeManager(this);
        questManager = new QuestManager(this);
        qPlayerManager = new QPlayerManager(this);

        dataGenerator();
        setupVersionSpecific();

        Bukkit.getPluginCommand("quests").setExecutor(new CommandQuests(this));
        Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new EventInventory(this), this);
        Bukkit.getPluginManager().registerEvents(new EventPlayerLeave(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder = new QuestsPlaceholders(this);
            placeholder.register();
        }

        Metrics metrics = new Metrics(this);
        if (metrics.isEnabled()) {
            this.getQuestsLogger().info("Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        questsConfigLoader = new QuestsConfigLoader(this);

        // register task types after the server has fully started
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
            taskTypeManager.registerTaskType(new PermissionTaskType());
            taskTypeManager.registerTaskType(new DistancefromTaskType());
            taskTypeManager.registerTaskType(new CommandTaskType());
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
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                taskTypeManager.registerTaskType(new PlaceholderAPIEvaluateTaskType());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                taskTypeManager.registerTaskType(new EssentialsMoneyEarnTaskType());
                taskTypeManager.registerTaskType(new EssentialsBalanceTaskType());
            }

            taskTypeManager.closeRegistrations();
            reloadQuests();
//            if (!questsConfigLoader.getBrokenFiles().isEmpty()) {
//                this.getQuestsLogger().severe("Quests has failed to load the following files:");
//                for (Map.Entry<String, QuestsConfigLoader.ConfigLoadError> entry : questsConfigLoader.getBrokenFiles().entrySet()) {
//                    this.getQuestsLogger().severe(" - " + entry.getKey() + ": " + entry.getValue().getMessage());
//                }
//            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                qPlayerManager.loadPlayer(player.getUniqueId());
            }
        });

        // this intentionally should not be documented
        boolean ignoreUpdates = false;
        try {
            ignoreUpdates = new File(this.getDataFolder() + File.separator + "stfuQuestsUpdate").exists();
        } catch (Throwable ignored) { }

        Bukkit.getScheduler().runTaskTimer(this, questCompleter, 1L, 1L);

        updater = new Updater(this);
        if (!ignoreUpdates) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                updater.check();
            });
        }
    }

    @Override
    public void onDisable() {
        for (TaskType taskType : getTaskTypeManager().getTaskTypes()) {
            try {
                taskType.onDisable();
            } catch (Exception ignored) { }
        }
        for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
            qPlayer.getQuestProgressFile().saveToDisk(true);
        }
        if (placeholder != null) placeholder.unregister();
    }

    public void reloadQuests() {
        questManager.getQuests().clear();
        questManager.getCategories().clear();
        taskTypeManager.resetTaskTypes();

        questsConfigLoader.loadConfig();

        long autocompleteInterval = 12000;
        long completerPollInterval = 100;
        if (!isBrokenConfig()) {
            autocompleteInterval = this.getConfig().getLong("options.performance-tweaking.quest-autocomplete-interval", 12000);
            completerPollInterval = this.getConfig().getLong("options.performance-tweaking.quest-completer-poll-interval", 100);
        }
        if (questAutosaveTask != null) {
            try {
                questAutosaveTask.cancel();
            } catch (Exception ex) {
                questsLogger.debug("Cannot cancel quest autosave task");
            }
        }
        questAutosaveTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
                qPlayer.getQuestProgressFile().saveToDisk(false);
            }
        }, autocompleteInterval, autocompleteInterval);
    }

    public ItemStack getItemStack(String path, ConfigurationSection config, ItemGetter.Filter... excludes) {
        return itemGetter.getItem(path, config, this, excludes);
    }

    public ItemGetter getItemGetter() {
        return itemGetter;
    }

    private void setupVersionSpecific() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            getQuestsLogger().warning("Failed to resolve server version - some features will not work!");
            title = new Title_Other();
            itemGetter = new ItemGetter_Late_1_8();
            return;
        }

        getQuestsLogger().info("Your server is running version " + version + ".");

        if (version.startsWith("v1_7")) {
            title = new Title_Other();
        } else if (version.startsWith("v1_8") || version.startsWith("v1_9") || version.startsWith("v1_10")) {
            title = new Title_BukkitNoTimings();
        } else {
            title = new Title_Bukkit();
        }

        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")) {
            itemGetter = new ItemGetter_Late_1_8();
        } else if (version.startsWith("v1_13")) {
            itemGetter = new ItemGetter_1_13();
        } else {
            itemGetter = new ItemGetterLatest();
        }

        if (title instanceof Title_Bukkit) {
            getQuestsLogger().info("Titles have been enabled.");
        } else if (title instanceof Title_BukkitNoTimings) {
            getQuestsLogger().info("Titles have been enabled, although they have limited timings.");
        } else {
            getQuestsLogger().info("Titles are not supported for this version.");
        }
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
            examples.add("example7.yml");
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

    public QuestCompleter getQuestCompleter() {
        return questCompleter;
    }

    public QuestsLogger getQuestsLogger() {
        return questsLogger;
    }
}
