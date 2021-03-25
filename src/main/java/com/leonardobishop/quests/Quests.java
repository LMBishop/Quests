package com.leonardobishop.quests;

import com.leonardobishop.quests.bstats.Metrics;
import com.leonardobishop.quests.commands.CommandQuests;
import com.leonardobishop.quests.events.EventPlayerJoin;
import com.leonardobishop.quests.events.EventPlayerLeave;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.hooks.coreprotect.CoreProtectHook;
import com.leonardobishop.quests.hooks.coreprotect.CoreProtectNoHook;
import com.leonardobishop.quests.hooks.coreprotect.ICoreProtectHook;
import com.leonardobishop.quests.hooks.itemgetter.ItemGetter;
import com.leonardobishop.quests.hooks.itemgetter.ItemGetterLatest;
import com.leonardobishop.quests.hooks.itemgetter.ItemGetter_1_13;
import com.leonardobishop.quests.hooks.itemgetter.ItemGetter_Late_1_8;
import com.leonardobishop.quests.hooks.papi.IPlaceholderAPIHook;
import com.leonardobishop.quests.hooks.papi.PlaceholderAPIHook;
import com.leonardobishop.quests.hooks.title.Title;
import com.leonardobishop.quests.hooks.title.Title_Bukkit;
import com.leonardobishop.quests.hooks.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.hooks.title.Title_Other;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.QPlayerManager;
import com.leonardobishop.quests.quests.QuestManager;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import com.leonardobishop.quests.quests.tasktypes.types.BreedingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.BrewingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.BuildingCertainTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.BuildingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.CommandTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.DealDamageTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.DistancefromTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.EnchantingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.ExpEarnTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.FishingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.InventoryTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.MilkingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.MiningCertainTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.MiningTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.MobkillingCertainTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.MobkillingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.PermissionTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.PlayerkillingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.PlaytimeTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.PositionTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.ShearingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.TamingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.WalkingTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.ASkyBlockLevelType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.BentoBoxLevelTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.CitizensDeliverTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.CitizensInteractTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.EssentialsBalanceTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.EssentialsMoneyEarnTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.IridiumSkyblockValueType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.MythicMobsKillingType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.PlaceholderAPIEvaluateTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.ShopGUIPlusBuyCertainTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.ShopGUIPlusSellCertainTaskType;
import com.leonardobishop.quests.quests.tasktypes.types.dependent.uSkyBlockLevelType;
import com.leonardobishop.quests.updater.Updater;
import com.leonardobishop.quests.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Quests extends JavaPlugin {

    private static Quests instance;

    /** Handles anything to do with loaded quests */
    private QuestManager questManager;
    /** Handles anything to do with task types */
    private TaskTypeManager taskTypeManager;
    /** Handles anything to do with players */
    private QPlayerManager qPlayerManager;

    /** Checks and records ready plugin updates */
    private Updater updater;
    /** Abstract title handle to allow for cross version compatibility */
    private Title titleHandle;
    /** Abstract item handle to allow for cross version compatibility */
    private ItemGetter itemGetter;
    /** Task which checks quests and marks them as complete if requirements are satisfied */
    private QuestCompleter questCompleter;
    /** Loads configurations and tracks errors */
    private QuestsConfigLoader questsConfigLoader;
    /** Quests logger to allow for configurable logging levels */
    private QuestsLogger questsLogger;
    /** Handles menu tracking and clicks */
    private MenuController menuController;

    private IPlaceholderAPIHook placeholderAPIHook;
    private ICoreProtectHook coreProtectHook;

    /** If true, the plugin should be inoperable */
    private boolean brokenConfig = false;
    private BukkitTask questAutosaveTask;
    private BukkitTask questQueuePollTask;

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

    public Title getTitleHandle() {
        return titleHandle;
    }

    public Updater getUpdater() {
        return updater;
    }

    public QuestsConfigLoader getQuestsConfigLoader() {
        return questsConfigLoader;
    }

    public MenuController getMenuController() {
        return menuController;
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
        instance = this;

        questsLogger = new QuestsLogger(this, QuestsLogger.LoggingLevel.INFO);
        questCompleter = new QuestCompleter(this);

        taskTypeManager = new TaskTypeManager(this);
        questManager = new QuestManager(this);
        qPlayerManager = new QPlayerManager(this);
        menuController = new MenuController(this);

        this.generateConfigurations();
        this.setupVersionSpecific();

        Bukkit.getPluginCommand("quests").setExecutor(new CommandQuests(this));
        Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(menuController, this);
        Bukkit.getPluginManager().registerEvents(new EventPlayerLeave(this), this);

        Metrics metrics = new Metrics(this);
        if (metrics.isEnabled()) {
            this.getQuestsLogger().info("Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        questsConfigLoader = new QuestsConfigLoader(this);

        // register task types after the server has fully started
        Bukkit.getScheduler().runTask(this, () -> {
            taskTypeManager.registerTaskType(new MiningTaskType());
            taskTypeManager.registerTaskType(new MiningCertainTaskType(this));
            taskTypeManager.registerTaskType(new BuildingTaskType());
            taskTypeManager.registerTaskType(new BuildingCertainTaskType());
            taskTypeManager.registerTaskType(new MobkillingTaskType());
            taskTypeManager.registerTaskType(new MobkillingCertainTaskType());
            taskTypeManager.registerTaskType(new PlayerkillingTaskType());
            taskTypeManager.registerTaskType(new FishingTaskType());
            taskTypeManager.registerTaskType(new InventoryTaskType(this));
            taskTypeManager.registerTaskType(new WalkingTaskType());
            taskTypeManager.registerTaskType(new TamingTaskType());
            taskTypeManager.registerTaskType(new MilkingTaskType());
            taskTypeManager.registerTaskType(new ShearingTaskType());
            taskTypeManager.registerTaskType(new PositionTaskType());
            taskTypeManager.registerTaskType(new PlaytimeTaskType(this));
            taskTypeManager.registerTaskType(new BrewingTaskType());
            taskTypeManager.registerTaskType(new ExpEarnTaskType());
            taskTypeManager.registerTaskType(new BreedingTaskType());
            taskTypeManager.registerTaskType(new EnchantingTaskType());
            taskTypeManager.registerTaskType(new DealDamageTaskType());
            taskTypeManager.registerTaskType(new PermissionTaskType(this));
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
                taskTypeManager.registerTaskType(new CitizensDeliverTaskType(this));
                taskTypeManager.registerTaskType(new CitizensInteractTaskType());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                taskTypeManager.registerTaskType(new MythicMobsKillingType());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                taskTypeManager.registerTaskType(new PlaceholderAPIEvaluateTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                taskTypeManager.registerTaskType(new EssentialsMoneyEarnTaskType());
                taskTypeManager.registerTaskType(new EssentialsBalanceTaskType());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("ShopGUIPlus")) {
                // not tested
                taskTypeManager.registerTaskType(new ShopGUIPlusBuyCertainTaskType());
                taskTypeManager.registerTaskType(new ShopGUIPlusSellCertainTaskType());
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

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderAPIHook = new PlaceholderAPIHook();
            this.placeholderAPIHook.registerExpansion(this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CoreProtect")) {
            this.coreProtectHook = new CoreProtectHook();
        } else {
            this.coreProtectHook = new CoreProtectNoHook();
        }

        // this intentionally should not be documented
        boolean ignoreUpdates = false;
        try {
            ignoreUpdates = new File(this.getDataFolder() + File.separator + "stfuQuestsUpdate").exists();
        } catch (Throwable ignored) { }


        updater = new Updater(this, !ignoreUpdates);
        if (!ignoreUpdates) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                updater.check();
            });
        }
    }

    public IPlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public ICoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    @Override
    public void onDisable() {
        for (TaskType taskType : getTaskTypeManager().getTaskTypes()) {
            try {
                taskType.onDisable();
            } catch (Exception ignored) { }
        }
        for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
            qPlayer.getQuestProgressFile().saveToDisk(false);
        }
        if (placeholderAPIHook != null) placeholderAPIHook.unregisterExpansion();
    }

    public void reloadQuests() {
        questManager.getQuests().clear();
        questManager.getCategories().clear();
        taskTypeManager.resetTaskTypes();

        questsConfigLoader.loadConfig();

        long autosaveInterval = 12000;
        if (!isBrokenConfig()) {
            autosaveInterval = this.getConfig().getLong("options.performance-tweaking.quest-autosave-interval", 12000);
        }
        boolean autosaveTaskCancelled = true;
        if (questAutosaveTask != null) {
            try {
                questAutosaveTask.cancel();
            } catch (Exception ex) {
                questsLogger.debug("Cannot cancel and restart quest autosave task");
                autosaveTaskCancelled = false;
            }
        }
        if (autosaveTaskCancelled) {
            questAutosaveTask = Bukkit.getScheduler().runTaskTimer(this, () -> new QuestsAutosaveRunnable(this), autosaveInterval, autosaveInterval);
        }

        boolean queuePollTaskCancelled = true;
        long queueExecuteInterval = 1;
        if (!isBrokenConfig()) {
            queueExecuteInterval = this.getConfig().getLong("options.performance-tweaking.quest-queue-executor-interval", 1);
        }
        if (questQueuePollTask != null) {
            try {
                questQueuePollTask.cancel();
            } catch (Exception ex) {
                questsLogger.debug("Cannot cancel and restart quest autosave task");
                queuePollTaskCancelled = false;
            }
        }
        if (queuePollTaskCancelled) {
            questQueuePollTask = Bukkit.getScheduler().runTaskTimer(this, questCompleter, queueExecuteInterval, queueExecuteInterval);
        }
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
            titleHandle = new Title_Other();
            itemGetter = new ItemGetter_Late_1_8();
            return;
        }

        getQuestsLogger().info("Your server is running version " + version + ".");

        if (version.startsWith("v1_7")) {
            titleHandle = new Title_Other();
        } else if (version.startsWith("v1_8") || version.startsWith("v1_9") || version.startsWith("v1_10")) {
            titleHandle = new Title_BukkitNoTimings();
        } else {
            titleHandle = new Title_Bukkit();
        }

        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")) {
            itemGetter = new ItemGetter_Late_1_8();
        } else if (version.startsWith("v1_13")) {
            itemGetter = new ItemGetter_1_13();
        } else {
            itemGetter = new ItemGetterLatest();
        }

        if (titleHandle instanceof Title_Bukkit) {
            getQuestsLogger().info("Titles have been enabled.");
        } else if (titleHandle instanceof Title_BukkitNoTimings) {
            getQuestsLogger().info("Titles have been enabled, although they have limited timings.");
        } else {
            getQuestsLogger().info("Titles are not supported for this version.");
        }
    }

    private void generateConfigurations() {
        File directory = new File(String.valueOf(this.getDataFolder()));
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            try {
                config.createNewFile();
                try (InputStream in = this.getResource("config.yml")) {
                    OutputStream out = new FileOutputStream(config);
                    byte[] buffer = new byte[1024];
                    int lenght = in.read(buffer);
                    while (lenght != -1) {
                        out.write(buffer, 0, lenght);
                        lenght = in.read(buffer);
                    }
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
                    try (InputStream in = this.getResource("quests/" + name)) {
                        OutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int lenght = in.read(buffer);
                        while (lenght != -1) {
                            out.write(buffer, 0, lenght);
                            lenght = in.read(buffer);
                        }
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
