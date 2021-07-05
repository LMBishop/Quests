package com.leonardobishop.quests.bukkit;

import com.leonardobishop.quests.bukkit.command.QuestsCommand;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsLoader;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.coreprotect.CoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.coreprotect.CoreProtectNoHook;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetterLatest;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter_1_13;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter_Late_1_8;
import com.leonardobishop.quests.bukkit.hook.papi.AbstractPlaceholderAPIHook;
import com.leonardobishop.quests.bukkit.hook.papi.PlaceholderAPIHook;
import com.leonardobishop.quests.bukkit.hook.title.Title;
import com.leonardobishop.quests.bukkit.hook.title.Title_Bukkit;
import com.leonardobishop.quests.bukkit.hook.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.bukkit.hook.title.Title_Other;
import com.leonardobishop.quests.bukkit.listener.PlayerJoinListener;
import com.leonardobishop.quests.bukkit.listener.PlayerLeaveListener;
import com.leonardobishop.quests.bukkit.menu.MenuController;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStackRegistry;
import com.leonardobishop.quests.bukkit.questcompleter.BukkitQuestCompleter;
import com.leonardobishop.quests.bukkit.questcontroller.NormalQuestController;
import com.leonardobishop.quests.bukkit.runnable.QuestsAutoSaveRunnable;
import com.leonardobishop.quests.bukkit.storage.MySqlStorageProvider;
import com.leonardobishop.quests.bukkit.storage.YamlStorageProvider;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskTypeManager;
import com.leonardobishop.quests.bukkit.tasktype.type.BreedingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BrewingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BuildingCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BuildingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CommandTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CraftingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.DealDamageTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.DistancefromTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.EnchantingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ExpEarnTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.FishingCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.FishingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.InventoryTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MilkingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MiningCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MiningTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MobkillingCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MobkillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PermissionTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PlayerkillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PlaytimeTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PositionTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ShearingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.TamingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.WalkingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ASkyBlockLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BentoBoxLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.CitizensDeliverTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.CitizensInteractTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EssentialsBalanceTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EssentialsMoneyEarnTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.IridiumSkyblockValueTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.MythicMobsKillingType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.PlaceholderAPIEvaluateTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ShopGUIPlusBuyCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ShopGUIPlusSellCertainTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.uSkyBlockLevelTaskType;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.config.QuestsConfig;
import com.leonardobishop.quests.common.logger.QuestsLogger;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.QuestCompleter;
import com.leonardobishop.quests.common.quest.QuestManager;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.scheduler.ServerScheduler;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import com.leonardobishop.quests.common.updater.Updater;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BukkitQuestsPlugin extends JavaPlugin implements Quests {

    private QuestsLogger questsLogger;
    private QuestManager questManager;
    private TaskTypeManager taskTypeManager;
    private QPlayerManager qPlayerManager;
    private QuestController questController;
    private QuestCompleter questCompleter;
    private BukkitQuestsConfig questsConfig;
    private Updater updater;
    private ServerScheduler serverScheduler;
    private StorageProvider storageProvider;

    private boolean validConfiguration;
    private Map<String, List<ConfigProblem>> configProblems;

    private QItemStackRegistry qItemStackRegistry;
    private MenuController menuController;
    private AbstractPlaceholderAPIHook placeholderAPIHook;
    private AbstractCoreProtectHook coreProtectHook;
    private ItemGetter itemGetter;
    private Title titleHandle;

    private BukkitTask questAutoSaveTask;
    private BukkitTask questQueuePollTask;

    @Override
    public @NotNull QuestsLogger getQuestsLogger() {
        return questsLogger;
    }

    @Override
    public @NotNull QuestManager getQuestManager() {
        return questManager;
    }

    @Override
    public @NotNull TaskTypeManager getTaskTypeManager() {
        return taskTypeManager;
    }

    @Override
    public @NotNull QPlayerManager getPlayerManager() {
        return qPlayerManager;
    }

    @Override
    public @NotNull QuestController getQuestController() {
        return questController;
    }

    @Override
    public @NotNull QuestCompleter getQuestCompleter() {
        return questCompleter;
    }

    @Override
    public @NotNull QuestsConfig getQuestsConfig() {
        return questsConfig;
    }

    @Override
    public @NotNull Updater getUpdater() {
        return updater;
    }

    @Override
    public @NotNull StorageProvider getStorageProvider() {
        return storageProvider;
    }

    @Override
    public @NotNull ServerScheduler getScheduler() {
        return serverScheduler;
    }

    @Override
    public void onEnable() {
        // Initial module initialization
        this.questsLogger = new BukkitQuestsLogger(this);
        this.generateConfigurations();
        this.questsConfig = new BukkitQuestsConfig(new File(super.getDataFolder() + File.separator + "config.yml"));
        this.questManager = new QuestManager(this);
        this.taskTypeManager = new BukkitTaskTypeManager(this);
        this.serverScheduler = new BukkitServerSchedulerAdapter(this);

        // Load base configuration for use during rest of startup procedure
        if (!this.reloadBaseConfiguration()) {
            questsLogger.severe("Plugin cannot start into a stable state as the configuration is broken!");
            super.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialise storage provider
        String configuredProvider = questsConfig.getString("options.storage.provider", "yaml");
        switch (configuredProvider.toLowerCase()) {
            default:
                questsLogger.warning("No valid storage provider is configured - Quests will use YAML storage as a default");
            case "yaml":
                this.storageProvider = new YamlStorageProvider(this);
                break;
            case "mysql":
                this.storageProvider = new MySqlStorageProvider(this, this.getConfig().getConfigurationSection("options.storage.database-settings"));
        }

        try {
            storageProvider.init();
        } catch (Exception e) {
            questsLogger.severe("An error occurred initialising the storage provider.");
            e.printStackTrace();
        }

        // Setup version specific compatibility layers
        int version;
        try {
            version = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            questsLogger.warning("Failed to resolve server version - some features may not work!");
            version = 0;
        }
        questsLogger.info("Your server is running version 1." + version);
        // (titles)
        if (version < 8) {
            titleHandle = new Title_Other();
        } else if (version <= 10) {
            titleHandle = new Title_BukkitNoTimings();
        } else {
            titleHandle = new Title_Bukkit();
        }
        // (itemstacks)
        if (version <= 12) {
            itemGetter = new ItemGetter_Late_1_8();
        } else if (version == 13) {
            itemGetter = new ItemGetter_1_13();
        } else {
            itemGetter = new ItemGetterLatest();
        }

        questsConfig.setItemGetter(itemGetter);

        // Finish module initialisation
        this.qPlayerManager = new QPlayerManager(this, storageProvider, questController);
        this.menuController = new MenuController(this);
        this.qItemStackRegistry = new QItemStackRegistry();
        this.questCompleter = new BukkitQuestCompleter(this);

        // Start metrics
        MetricsLite metrics = new MetricsLite(this, 3443);
        if (metrics.isEnabled()) {
            this.getQuestsLogger().info("Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        // Setup external plugin hooks
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderAPIHook = new PlaceholderAPIHook();
            this.placeholderAPIHook.registerExpansion(this);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CoreProtect")) {
            this.coreProtectHook = new CoreProtectHook();
        } else {
            this.coreProtectHook = new CoreProtectNoHook();
        }

        // Start quests update checker
        boolean ignoreUpdates = false;
        try {
            ignoreUpdates = new File(this.getDataFolder() + File.separator + "stfuQuestsUpdate").exists();
        } catch (Throwable ignored) { }
        this.updater = new Updater(this, super.getDescription().getVersion(), !ignoreUpdates);
        if (!ignoreUpdates) {
            serverScheduler.doAsync(() -> updater.check());
        }

        // Register commands
        super.getCommand("quests").setExecutor(new QuestsCommand(this));

        // Register events
        super.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        super.getServer().getPluginManager().registerEvents(menuController, this);
        super.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        // Register task types after the server has fully started
        Bukkit.getScheduler().runTask(this, () -> {
            taskTypeManager.registerTaskType(new MiningTaskType(this));
            taskTypeManager.registerTaskType(new MiningCertainTaskType(this));
            taskTypeManager.registerTaskType(new BuildingTaskType(this));
            taskTypeManager.registerTaskType(new BuildingCertainTaskType(this));
            taskTypeManager.registerTaskType(new MobkillingTaskType(this));
            taskTypeManager.registerTaskType(new MobkillingCertainTaskType(this));
            taskTypeManager.registerTaskType(new PlayerkillingTaskType(this));
            taskTypeManager.registerTaskType(new FishingTaskType(this));
            taskTypeManager.registerTaskType(new FishingCertainTaskType(this));
            taskTypeManager.registerTaskType(new InventoryTaskType(this));
            taskTypeManager.registerTaskType(new WalkingTaskType(this));
            taskTypeManager.registerTaskType(new TamingTaskType(this));
            taskTypeManager.registerTaskType(new MilkingTaskType(this));
            taskTypeManager.registerTaskType(new ShearingTaskType(this));
            taskTypeManager.registerTaskType(new PositionTaskType(this));
            taskTypeManager.registerTaskType(new PlaytimeTaskType(this));
            taskTypeManager.registerTaskType(new BrewingTaskType(this));
            taskTypeManager.registerTaskType(new ExpEarnTaskType(this));
            taskTypeManager.registerTaskType(new BreedingTaskType(this));
            taskTypeManager.registerTaskType(new EnchantingTaskType(this));
            taskTypeManager.registerTaskType(new DealDamageTaskType(this));
            taskTypeManager.registerTaskType(new PermissionTaskType(this));
            taskTypeManager.registerTaskType(new DistancefromTaskType(this));
            taskTypeManager.registerTaskType(new CommandTaskType(this));
            taskTypeManager.registerTaskType(new CraftingTaskType(this));
            // TODO: FIX
            // taskTypeManager.registerTaskType(new BrewingCertainTaskType());
            if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock")) {
                taskTypeManager.registerTaskType(new ASkyBlockLevelTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
                BentoBoxLevelTaskType.register(this, taskTypeManager);
            }
            //TODO FIX
            if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock")
                    && Bukkit.getPluginManager().getPlugin("IridiumSkyblock").getDescription().getVersion().startsWith("2")) {
                taskTypeManager.registerTaskType(new IridiumSkyblockValueTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("uSkyBlock")) {
                taskTypeManager.registerTaskType(new uSkyBlockLevelTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                taskTypeManager.registerTaskType(new CitizensDeliverTaskType(this));
                taskTypeManager.registerTaskType(new CitizensInteractTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                taskTypeManager.registerTaskType(new MythicMobsKillingType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                taskTypeManager.registerTaskType(new PlaceholderAPIEvaluateTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                taskTypeManager.registerTaskType(new EssentialsMoneyEarnTaskType(this));
                taskTypeManager.registerTaskType(new EssentialsBalanceTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("ShopGUIPlus")) {
                // not tested
                taskTypeManager.registerTaskType(new ShopGUIPlusBuyCertainTaskType(this));
                taskTypeManager.registerTaskType(new ShopGUIPlusSellCertainTaskType(this));
            }

            taskTypeManager.closeRegistrations();
            questsLogger.info(taskTypeManager.getTaskTypes().size() + " task types have been registered.");

            reloadQuests();

            // Load players who were present during startup (i.e some idiot reloaded the server instead of restarted)
            for (Player player : Bukkit.getOnlinePlayers()) {
                qPlayerManager.loadPlayer(player.getUniqueId());
            }
        });
    }

    @Override
    public void onDisable() {
        if (!validConfiguration) return;

        for (TaskType taskType : getTaskTypeManager().getTaskTypes()) {
            try {
                taskType.onDisable();
            } catch (Exception ignored) { }
        }
        for (QPlayer qPlayer : qPlayerManager.getQPlayers()) {
            try {
                qPlayerManager.savePlayerSync(qPlayer.getPlayerUUID());
            } catch (Exception ignored) { }
        }
        if (placeholderAPIHook != null) {
            try {
                placeholderAPIHook.unregisterExpansion();
            } catch (Exception e) {
                questsLogger.warning("You need to update PlaceholderAPI for Quests to exit gracefully:");
                e.printStackTrace();
            }
        }
        try {
            qPlayerManager.getStorageProvider().shutdown();
        } catch (Exception ignored) { }
    }

    @Override
    public void reloadQuests() {
        if (this.reloadBaseConfiguration()) {
            BukkitQuestsLoader questsLoader = new BukkitQuestsLoader(this);
            configProblems = questsLoader.loadQuests(new File(super.getDataFolder() + File.separator + "quests"));

            for (TaskType taskType : taskTypeManager.getTaskTypes()) {
                try {
                    taskType.onReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            configProblems = Collections.singletonMap("<MAIN CONFIG> config.yml",
                    Collections.singletonList(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.MALFORMED_YAML.getDescription())));
        }
    }

    public ItemStack getItemStack(String path, ConfigurationSection config, ItemGetter.Filter... excludes) {
        return itemGetter.getItem(path, config, excludes);
    }

    private boolean reloadBaseConfiguration() {
        this.validConfiguration = questsConfig.loadConfig();

        if (validConfiguration) {
            int loggingLevel = questsConfig.getInt("options.verbose-logging-level", 2);
            questsLogger.setServerLoggingLevel(QuestsLogger.LoggingLevel.fromNumber(loggingLevel));

            switch (questsConfig.getString("quest-mode.mode", "normal").toLowerCase()) {
                default:
                case "normal":
                    questController = new NormalQuestController(this);
                    //TODO the other one
            }

            long autoSaveInterval = this.getConfig().getLong("options.performance-tweaking.quest-autosave-interval", 12000);
            try {
                if (questAutoSaveTask != null) questAutoSaveTask.cancel();
                questAutoSaveTask = Bukkit.getScheduler().runTaskTimer(this, () -> new QuestsAutoSaveRunnable(this), autoSaveInterval, autoSaveInterval);
            } catch (Exception ex) {
                questsLogger.debug("Cannot cancel and restart quest autosave task");
            }

            long queueExecuteInterval = this.getConfig().getLong("options.performance-tweaking.quest-queue-executor-interval", 1);
            try {
                if (questQueuePollTask != null) questQueuePollTask.cancel();
                questQueuePollTask = Bukkit.getScheduler().runTaskTimer(this, (BukkitQuestCompleter) questCompleter, queueExecuteInterval, queueExecuteInterval);
            } catch (Exception ex) {
                questsLogger.debug("Cannot cancel and restart queue executor task");
            }
        }
        return validConfiguration;
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
                try (InputStream in = BukkitQuestsPlugin.class.getClassLoader().getResourceAsStream("resources/bukkit/config.yml");
                     OutputStream out = new FileOutputStream(config)) {
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
                    try (InputStream in = BukkitQuestsPlugin.class.getClassLoader().getResourceAsStream("resources/bukkit/quests/" + name);
                         OutputStream out = new FileOutputStream(file)) {
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

    public boolean isValidConfiguration() {
        return validConfiguration;
    }

    public Map<String, List<ConfigProblem>> getConfigProblems() {
        return configProblems;
    }

    public AbstractPlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public AbstractCoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    public ItemGetter getItemGetter() {
        return itemGetter;
    }

    public Title getTitleHandle() {
        return titleHandle;
    }

    public QItemStackRegistry getQItemStackRegistry() {
        return qItemStackRegistry;
    }

    public MenuController getMenuController() {
        return menuController;
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return questsConfig.getConfig();
    }

    @Override
    public void reloadConfig() {
        this.reloadBaseConfiguration();
    }
}
