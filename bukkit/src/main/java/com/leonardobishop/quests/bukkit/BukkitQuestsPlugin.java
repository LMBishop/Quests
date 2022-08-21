package com.leonardobishop.quests.bukkit;

import com.leonardobishop.quests.bukkit.command.QuestsCommandSwitcher;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsLoader;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.coreprotect.CoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.essentials.AbstractEssentialsHook;
import com.leonardobishop.quests.bukkit.hook.essentials.EssentialsHook;
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
import com.leonardobishop.quests.bukkit.hook.versionspecific.*;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItemRegistry;
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
import com.leonardobishop.quests.bukkit.tasktype.type.*;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.*;
import com.leonardobishop.quests.bukkit.util.LogHistory;
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
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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
    private QuestItemRegistry questItemRegistry;
    private MenuController menuController;
    private AbstractPlaceholderAPIHook placeholderAPIHook;
    private AbstractCoreProtectHook coreProtectHook;
    private AbstractEssentialsHook essentialsHook;
    private ItemGetter itemGetter;
    private Title titleHandle;
    private VersionSpecificHandler versionSpecificHandler;

    private LogHistory logHistory;
    private BukkitTask questAutoSaveTask;
    private BukkitTask questQueuePollTask;
    private BiFunction<Player, String, String> placeholderAPIProcessor;

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
        this.logHistory = new LogHistory(true);
        this.generateConfigurations();
        this.questsConfig = new BukkitQuestsConfig(new File(super.getDataFolder() + File.separator + "config.yml"));
        this.questManager = new QuestManager(this);
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
                ConfigurationSection section = this.getConfig().getConfigurationSection("options.storage.database-settings");
                if (section == null) {
                    questsLogger.warning("No database settings are configured - default values will be used");
                }
                this.storageProvider = new MySqlStorageProvider(this, section);
        }

        try {
            questsLogger.info("Initialising storage provider '" + storageProvider.getName() + "'");
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
        // (version specific handler)
        // TODO move above to version specific handlers
        if (version <= 8) {
            versionSpecificHandler = new VersionSpecificHandler8();
        } else switch (version) {
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                versionSpecificHandler = new VersionSpecificHandler9();
                break;
            case 14:
            case 15:
                versionSpecificHandler = new VersionSpecificHandler14();
                break;
            default:
                versionSpecificHandler = new VersionSpecificHandler16();
                break;
        }

        questsConfig.setItemGetter(itemGetter);

        // Finish module initialisation
        this.taskTypeManager = new BukkitTaskTypeManager(this, questsConfig.getStringList("options.task-type-exclusions"));
        this.qPlayerManager = new QPlayerManager(this, storageProvider, questController);
        this.menuController = new MenuController(this);
        this.questItemRegistry = new QuestItemRegistry();
        this.qItemStackRegistry = new QItemStackRegistry();
        this.questCompleter = new BukkitQuestCompleter(this);

        // Start metrics
        MetricsLite metrics = new MetricsLite(this, 3443);
        if (metrics.isEnabled()) {
            this.getQuestsLogger().info("Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        // Prepare PAPI processor
        this.placeholderAPIProcessor = (player, s) -> s;

        // Start quests update checker
        boolean ignoreUpdates = false;
        try {
            ignoreUpdates = new File(this.getDataFolder() + File.separator + "stfuQuestsUpdate").exists();
        } catch (Throwable ignored) { }
        this.updater = new Updater(this, super.getDescription().getVersion(), !ignoreUpdates);
        if (!ignoreUpdates) {
            serverScheduler.doAsync(() -> updater.check());
        }

        // Set commands
        QuestsCommandSwitcher questsCommandSwitcher = new QuestsCommandSwitcher(this);
        super.getCommand("quests").setTabCompleter(questsCommandSwitcher);
        super.getCommand("quests").setExecutor(questsCommandSwitcher);

        // Register events
        super.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        super.getServer().getPluginManager().registerEvents(menuController, this);
        super.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        // Register task types after the server has fully started
        Bukkit.getScheduler().runTask(this, () -> {
            // Setup external plugin hooks
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                this.placeholderAPIHook = new PlaceholderAPIHook();
                this.placeholderAPIHook.registerExpansion(this);
                this.placeholderAPIProcessor = (player, s) -> placeholderAPIHook.replacePlaceholders(player, s);
            }
            if (Bukkit.getPluginManager().isPluginEnabled("CoreProtect")) {
                this.coreProtectHook = new CoreProtectHook(this);
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                this.essentialsHook = new EssentialsHook();
            }

            taskTypeManager.registerTaskType(new MiningTaskType(this));
            taskTypeManager.registerTaskType(new BuildingTaskType(this));
            taskTypeManager.registerTaskType(new MobkillingTaskType(this));
            taskTypeManager.registerTaskType(new PlayerkillingTaskType(this));
            taskTypeManager.registerTaskType(new FishingTaskType(this));
            taskTypeManager.registerTaskType(new SmeltingTaskType(this));
            taskTypeManager.registerTaskType(new InventoryTaskType(this));
            taskTypeManager.registerTaskType(new ConsumeTaskType(this));
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
            taskTypeManager.registerTaskType(new BucketEmptyTaskType(this));
            taskTypeManager.registerTaskType(new BucketFillTaskType(this));
            taskTypeManager.registerTaskType(new InteractTaskType(this));
            taskTypeManager.registerTaskType(new SmithTaskType(this));
            // TODO: FIX
            // taskTypeManager.registerTaskType(new BrewingCertainTaskType());
            try {
                Class.forName("org.bukkit.block.data.Ageable");
                taskTypeManager.registerTaskType(new FarmingTaskType(this));
            } catch (ClassNotFoundException ignored) { } // server version cannot support task type
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
                String mythicMobsVersion = Bukkit.getPluginManager().getPlugin("MythicMobs").getDescription().getVersion();
                if (mythicMobsVersion.startsWith("4") || mythicMobsVersion.startsWith("5")) {
                    taskTypeManager.registerTaskType(new MythicMobsKillingTaskType(this, mythicMobsVersion));
                }
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
                taskTypeManager.registerTaskType(new ShopGUIPlusBuyTaskType(this));
                taskTypeManager.registerTaskType(new ShopGUIPlusSellTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("FabledSkyblock")) {
                // not tested
                taskTypeManager.registerTaskType(new FabledSkyblockLevelTaskType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
                // not tested
                taskTypeManager.registerTaskType(new SuperiorSkyblockLevelType(this));
                taskTypeManager.registerTaskType(new SuperiorSkyblockWorthType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("VotingPlugin")) {
                // not tested
                taskTypeManager.registerTaskType(new VotingPluginVoteType(this));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Votifier")) {
                // not tested
                taskTypeManager.registerTaskType(new NuVotifierVoteTaskType(this));
            }

            taskTypeManager.closeRegistrations();
            questsLogger.info(taskTypeManager.getTaskTypes().size() + " task types have been registered"
                    + (taskTypeManager.getSkipped() > 0 ? " (" + taskTypeManager.getSkipped() + " skipped due to exclusions or conflicting names)." : "."));

            reloadQuests();
            if (!this.getConfigProblems().isEmpty()) {
                questsLogger.warning("You have configuration issues preventing some quests from loading.");
                questsLogger.warning("You can view these issues with the command: /q a config.");
            }

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
            questsLoader.loadQuestItems(new File(super.getDataFolder() + File.separator + "items"));
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
                    Collections.singletonList(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, ConfigProblemDescriptions.MALFORMED_YAML.getDescription(), ConfigProblemDescriptions.MALFORMED_YAML.getExtendedDescription())));
        }
    }

    public QuestItem getConfiguredQuestItem(String path, ConfigurationSection config, ItemGetter.Filter... excludes) {
        if (config.contains(path + ".quest-item")) {
            return questItemRegistry.getItem(config.getString(path + ".quest-item"));
        }

        return new ParsedQuestItem("defined", null, getConfiguredItemStack(path, config, excludes));
    }


    public ItemStack getConfiguredItemStack(String path, ConfigurationSection config, ItemGetter.Filter... excludes) {
        return itemGetter.getItem(path, config, excludes);
    }

    private boolean reloadBaseConfiguration() {
        this.validConfiguration = questsConfig.loadConfig();

        if (validConfiguration) {
            int loggingLevel = questsConfig.getInt("options.verbose-logging-level", 2);
            questsLogger.setServerLoggingLevel(QuestsLogger.LoggingLevel.fromNumber(loggingLevel));
            boolean logHistoryEnabled = questsConfig.getBoolean("options.record-log-history", true);
            logHistory.setEnabled(logHistoryEnabled);

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
            writeResourceToFile("resources/bukkit/config.yml", config);
        }
        File categories = new File(this.getDataFolder() + File.separator + "categories.yml");
        if (!categories.exists()) {
            writeResourceToFile("resources/bukkit/categories.yml", categories);
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
                writeResourceToFile("resources/bukkit/quests/" + name, file);
            }
        }

        File itemsDirectory = new File(this.getDataFolder() + File.separator + "items");
        if (!itemsDirectory.exists() && !itemsDirectory.isDirectory()) {
            itemsDirectory.mkdir();
        }
    }

    public void writeResourceToFile(String resource, File file) {
        try {
            file.createNewFile();
            try (InputStream in = BukkitQuestsPlugin.class.getClassLoader().getResourceAsStream(resource);
                 OutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length = in.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = in.read(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidConfiguration() {
        return validConfiguration;
    }

    public Map<String, List<ConfigProblem>> getConfigProblems() {
        return configProblems;
    }

    public @NotNull BiFunction<Player, String, String> getPlaceholderAPIProcessor() {
        return placeholderAPIProcessor;
    }

    public @Nullable AbstractPlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public @Nullable AbstractCoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    public @Nullable AbstractEssentialsHook getEssentialsHook() {
        return essentialsHook;
    }

    public ItemGetter getItemGetter() {
        return itemGetter;
    }

    public Title getTitleHandle() {
        return titleHandle;
    }

    public VersionSpecificHandler getVersionSpecificHandler() {
        return versionSpecificHandler;
    }

    public QuestItemRegistry getQuestItemRegistry() {
        return questItemRegistry;
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

    public LogHistory getLogHistory() {
        return logHistory;
    }

    @Override
    public void reloadConfig() {
        this.reloadBaseConfiguration();
    }
}
