package com.leonardobishop.quests.bukkit;

import com.leonardobishop.quests.bukkit.command.QuestsCommandSwitcher;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsLoader;
import com.leonardobishop.quests.bukkit.hook.actionbar.ActionBar_Nothing;
import com.leonardobishop.quests.bukkit.hook.actionbar.ActionBar_Paper;
import com.leonardobishop.quests.bukkit.hook.actionbar.ActionBar_Spigot;
import com.leonardobishop.quests.bukkit.hook.actionbar.QuestsActionBar;
import com.leonardobishop.quests.bukkit.hook.bossbar.BossBar_Bukkit;
import com.leonardobishop.quests.bukkit.hook.bossbar.BossBar_Nothing;
import com.leonardobishop.quests.bukkit.hook.bossbar.QuestsBossBar;
import com.leonardobishop.quests.bukkit.hook.cmi.AbstractCMIHook;
import com.leonardobishop.quests.bukkit.hook.cmi.CMIHook;
import com.leonardobishop.quests.bukkit.hook.coreprotect.AbstractCoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.coreprotect.CoreProtectHook;
import com.leonardobishop.quests.bukkit.hook.essentials.AbstractEssentialsHook;
import com.leonardobishop.quests.bukkit.hook.essentials.EssentialsHook;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter13;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter14;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter20;
import com.leonardobishop.quests.bukkit.hook.itemgetter.ItemGetter8;
import com.leonardobishop.quests.bukkit.hook.papi.AbstractPlaceholderAPIHook;
import com.leonardobishop.quests.bukkit.hook.papi.PlaceholderAPIHook;
import com.leonardobishop.quests.bukkit.hook.playerblocktracker.AbstractPlayerBlockTrackerHook;
import com.leonardobishop.quests.bukkit.hook.playerblocktracker.PlayerBlockTrackerHook;
import com.leonardobishop.quests.bukkit.hook.skullgetter.BukkitSkullGetter;
import com.leonardobishop.quests.bukkit.hook.skullgetter.LegacySkullGetter;
import com.leonardobishop.quests.bukkit.hook.skullgetter.ModernSkullGetter;
import com.leonardobishop.quests.bukkit.hook.skullgetter.PaperSkullGetter;
import com.leonardobishop.quests.bukkit.hook.skullgetter.SkullGetter;
import com.leonardobishop.quests.bukkit.hook.title.QuestsTitle;
import com.leonardobishop.quests.bukkit.hook.title.Title_Bukkit;
import com.leonardobishop.quests.bukkit.hook.title.Title_BukkitNoTimings;
import com.leonardobishop.quests.bukkit.hook.title.Title_Nothing;
import com.leonardobishop.quests.bukkit.hook.vault.AbstractVaultHook;
import com.leonardobishop.quests.bukkit.hook.vault.VaultHook;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler11;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler16;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler17;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler20;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler8;
import com.leonardobishop.quests.bukkit.hook.versionspecific.VersionSpecificHandler9;
import com.leonardobishop.quests.bukkit.hook.wildstacker.AbstractWildStackerHook;
import com.leonardobishop.quests.bukkit.hook.wildstacker.WildStackerHook;
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
import com.leonardobishop.quests.bukkit.scheduler.ServerScheduler;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import com.leonardobishop.quests.bukkit.scheduler.bukkit.BukkitServerSchedulerAdapter;
import com.leonardobishop.quests.bukkit.scheduler.folia.FoliaServerScheduler;
import com.leonardobishop.quests.bukkit.storage.MySqlStorageProvider;
import com.leonardobishop.quests.bukkit.storage.YamlStorageProvider;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskTypeManager;
import com.leonardobishop.quests.bukkit.tasktype.type.BarteringTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BlockItemdroppingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BlockshearingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BreedingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BrewingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BucketEmptyTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BucketEntityTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BucketFillTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.BuildingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CommandTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CompostingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ConsumeTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CraftingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.CuringTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.DealDamageTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.DistancefromTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.EnchantingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ExpEarnTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.FarmingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.FishingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.HatchingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.InteractTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.InventoryTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ItembreakingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ItemdamagingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ItemmendingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MilkingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MiningTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.MobkillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PermissionTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PlayerkillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PlaytimeTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.PositionTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ProjectilelaunchingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ReplenishingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ResurrectingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.ShearingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.SmeltingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.SmithingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.TamingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.TradingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.WalkingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ASkyBlockLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BentoBoxLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.CitizensDeliverTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.CitizensInteractTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.CustomFishingFishingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EcoBossesKillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EcoMobsKillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EssentialsBalanceTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.EssentialsMoneyEarnTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.FabledSkyBlockLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.IridiumSkyblockValueTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.MythicMobsKillingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.NuVotifierVoteTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.PinataPartyHitTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.PlaceholderAPIEvaluateTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.PlayerPointsEarnTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.PyroFishingProFishingTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ServerNPCDeliverTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ServerNPCInteractTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ShopGUIPlusBuyTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ShopGUIPlusSellTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.SuperiorSkyblockLevelType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.SuperiorSkyblockWorthType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.VotingPluginVoteType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ZNPCsPlusDeliverTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.ZNPCsPlusInteractTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.uSkyBlockLevelTaskType;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BedWars1058BedBreakTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BedWars1058FinalKillTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BedWars1058WinTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.BedWars1058LoseTask;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
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
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import com.leonardobishop.quests.common.updater.Updater;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Level;

public class BukkitQuestsPlugin extends JavaPlugin implements Quests {

    private QuestsLogger questsLogger;
    private QuestManager questManager;
    private TaskTypeManager taskTypeManager;
    private QPlayerManager qPlayerManager;
    private QuestController questController;
    private BukkitQuestCompleter questCompleter;
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
    private AbstractCMIHook cmiHook;
    private AbstractCoreProtectHook coreProtectHook;
    private AbstractEssentialsHook essentialsHook;
    private AbstractPlayerBlockTrackerHook playerBlockTrackerHook;
    private AbstractVaultHook vaultHook;
    private AbstractWildStackerHook wildStackerHook;
    private ItemGetter itemGetter;
    private SkullGetter skullGetter;
    private QuestsTitle titleHandle;
    private QuestsBossBar bossBarHandle;
    private QuestsActionBar actionBarHandle;
    private VersionSpecificHandler versionSpecificHandler;

    private LogHistory logHistory;
    private WrappedTask questAutoSaveTask;
    private WrappedTask questQueuePollTask;
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

        this.serverScheduler = FoliaServerScheduler.FOLIA ? new FoliaServerScheduler(this) : new BukkitServerSchedulerAdapter(this);
        questsLogger.info("Running server scheduler: " + serverScheduler.getServerSchedulerName());

        // Load base configuration for use during rest of startup procedure
        if (!this.reloadBaseConfiguration(true)) {
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
            version = this.getServerVersion();
            this.questsLogger.info("Your server is running version 1." + version);
        } catch (final IllegalArgumentException e) {
            // all server supported versions by Quests fulfill this format,
            // so we assume that some future version can possibly break it,
            // and we want to load the latest and not the oldest handler
            version = Integer.MAX_VALUE;

            this.questsLogger.warning("Failed to resolve server version - some features may not work! (" + e.getMessage() + ")");
        }

        // (titles)
        this.setTitleHandle();

        // (bossbar)
        this.setBossBarHandle();

        // (actionbar)
        this.setActionBarHandle();

        // (itemstacks)
        this.setItemGetter();

        // (skulls)
        this.setSkullGetter();

        // (version specific handler)
        if (version <= 8) {
            this.versionSpecificHandler = new VersionSpecificHandler8();
        } else {
            this.versionSpecificHandler = switch (version) {
                case 9, 10 -> new VersionSpecificHandler9();
                case 11, 12, 13, 14, 15 -> new VersionSpecificHandler11();
                case 16 -> new VersionSpecificHandler16();
                case 17, 18, 19 -> new VersionSpecificHandler17();
                default -> new VersionSpecificHandler20();
            };
        }

        // Set item getter to be used by Quests config
        this.questsConfig.setItemGetter(this.itemGetter);

        // Finish module initialisation
        this.taskTypeManager = new BukkitTaskTypeManager(this, new HashSet<>(questsConfig.getStringList("options.task-type-exclusions")));
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
        getScheduler().doSync(() -> {
            // Setup external plugin hooks
            if (CompatUtils.isPluginEnabled("PlaceholderAPI")) {
                this.placeholderAPIHook = new PlaceholderAPIHook();
                this.placeholderAPIHook.registerExpansion(this);
                this.placeholderAPIProcessor = (player, s) -> placeholderAPIHook.replacePlaceholders(player, s);
            }

            if (CompatUtils.isPluginEnabled("CMI")) {
                this.cmiHook = new CMIHook();
            }

            if (CompatUtils.isPluginEnabled("CoreProtect")) {
                this.coreProtectHook = new CoreProtectHook(this);
            }

            if (CompatUtils.isPluginEnabled("Essentials")) {
                this.essentialsHook = new EssentialsHook();
            }

            try {
                String className = questsConfig.getString("options.playerblocktracker-class-name", "com.gestankbratwurst.playerblocktracker.PlayerBlockTracker");

                //noinspection unchecked
                Class<? extends Plugin> pluginClazz = (Class<? extends Plugin>) Class.forName(className);
                Method isTrackedMethod = pluginClazz.getMethod("isTracked", Block.class);
                this.playerBlockTrackerHook = new PlayerBlockTrackerHook(pluginClazz, isTrackedMethod);
            } catch (ClassCastException | ClassNotFoundException | NoSuchMethodException ignored) {
            }

            // Plugin checks are handled in the hook class
            this.vaultHook = new VaultHook(this);

            if (CompatUtils.isPluginEnabled("WildStacker")) {
                this.wildStackerHook = new WildStackerHook();
            }

            // Register task types without compatibility requirement
            taskTypeManager.registerTaskType(new BreedingTaskType(this));
            taskTypeManager.registerTaskType(new BucketEmptyTaskType(this));
            taskTypeManager.registerTaskType(new BucketFillTaskType(this));
            taskTypeManager.registerTaskType(new BuildingTaskType(this));
            taskTypeManager.registerTaskType(new CommandTaskType(this));
            taskTypeManager.registerTaskType(new ConsumeTaskType(this));
            taskTypeManager.registerTaskType(new CraftingTaskType(this));
            taskTypeManager.registerTaskType(new DealDamageTaskType(this));
            taskTypeManager.registerTaskType(new DistancefromTaskType(this));
            taskTypeManager.registerTaskType(new EnchantingTaskType(this));
            taskTypeManager.registerTaskType(new ExpEarnTaskType(this));
            taskTypeManager.registerTaskType(new FishingTaskType(this));
            taskTypeManager.registerTaskType(new InteractTaskType(this));
            taskTypeManager.registerTaskType(new InventoryTaskType(this));
            taskTypeManager.registerTaskType(new ItembreakingTaskType(this));
            taskTypeManager.registerTaskType(new ItemdamagingTaskType(this));
            taskTypeManager.registerTaskType(new MilkingTaskType(this));
            taskTypeManager.registerTaskType(new MiningTaskType(this));
            taskTypeManager.registerTaskType(new MobkillingTaskType(this));
            taskTypeManager.registerTaskType(new PermissionTaskType(this));
            taskTypeManager.registerTaskType(new PlayerkillingTaskType(this));
            taskTypeManager.registerTaskType(new PlaytimeTaskType(this));
            taskTypeManager.registerTaskType(new PositionTaskType(this));
            taskTypeManager.registerTaskType(new ProjectilelaunchingTaskType(this));
            taskTypeManager.registerTaskType(new ShearingTaskType(this));
            taskTypeManager.registerTaskType(new SmeltingTaskType(this));
            taskTypeManager.registerTaskType(new TamingTaskType(this));
            taskTypeManager.registerTaskType(new WalkingTaskType(this));

            // Register task types with class/method compatibility requirement
            taskTypeManager.registerTaskType(() -> new BarteringTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.entity.PiglinBarterEvent"));
            taskTypeManager.registerTaskType(() -> new BlockItemdroppingTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.block.BlockDropItemEvent"));
            taskTypeManager.registerTaskType(() -> new BlockshearingTaskType(this), () -> CompatUtils.classExists("io.papermc.paper.event.block.PlayerShearBlockEvent"));
            taskTypeManager.registerTaskType(() -> new BrewingTaskType(this), () -> CompatUtils.classWithMethodExists("org.bukkit.event.inventory.BrewEvent", "getResults"));
            taskTypeManager.registerTaskType(() -> new BucketEntityTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.player.PlayerBucketEntityEvent"));
            taskTypeManager.registerTaskType(() -> new CompostingTaskType(this), () -> CompatUtils.classExists("io.papermc.paper.event.entity.EntityCompostItemEvent"));
            taskTypeManager.registerTaskType(() -> new CuringTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.entity.EntityTransformEvent"));
            taskTypeManager.registerTaskType(() -> new FarmingTaskType(this), () -> CompatUtils.classExists("org.bukkit.block.data.Ageable"));
            taskTypeManager.registerTaskType(() -> new HatchingTaskType(this), () -> CompatUtils.classExists("com.destroystokyo.paper.event.entity.ThrownEggHatchEvent"));
            taskTypeManager.registerTaskType(() -> new ItemmendingTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.player.PlayerItemMendEvent"));
            taskTypeManager.registerTaskType(() -> new ReplenishingTaskType(this), () -> CompatUtils.classExists("com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent"));
            taskTypeManager.registerTaskType(() -> new ResurrectingTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.entity.EntityResurrectEvent"));
            taskTypeManager.registerTaskType(() -> new SmithingTaskType(this), () -> CompatUtils.classExists("org.bukkit.event.inventory.SmithItemEvent"));
            taskTypeManager.registerTaskType(() -> new TradingTaskType(this), () -> CompatUtils.classExists("io.papermc.paper.event.player.PlayerTradeEvent"));

            // Register task types with enabled plugin compatibility requirement
            taskTypeManager.registerTaskType(() -> new ASkyBlockLevelTaskType(this), () -> CompatUtils.isPluginEnabled("ASkyBlock"));
            taskTypeManager.registerTaskType(() -> new BentoBoxLevelTaskType(this), () -> CompatUtils.isPluginEnabled("BentoBox") && CompatUtils.classExists("world.bentobox.level.events.IslandLevelCalculatedEvent"));
            taskTypeManager.registerTaskType(() -> new CitizensDeliverTaskType(this), () -> CompatUtils.isPluginEnabled("Citizens"));
            taskTypeManager.registerTaskType(() -> new CitizensInteractTaskType(this), () -> CompatUtils.isPluginEnabled("Citizens"));
            taskTypeManager.registerTaskType(() -> new CustomFishingFishingTaskType(this), () -> CompatUtils.isPluginEnabled("CustomFishing"));
            taskTypeManager.registerTaskType(() -> new EcoBossesKillingTaskType(this), () -> CompatUtils.isPluginEnabled("EcoBosses"));
            taskTypeManager.registerTaskType(() -> new EcoMobsKillingTaskType(this), () -> CompatUtils.isPluginEnabled("EcoMobs"));
            taskTypeManager.registerTaskType(() -> new EssentialsBalanceTaskType(this), () -> CompatUtils.isPluginEnabled("Essentials"));
            taskTypeManager.registerTaskType(() -> new EssentialsMoneyEarnTaskType(this), () -> CompatUtils.isPluginEnabled("Essentials"));
            taskTypeManager.registerTaskType(() -> new FabledSkyBlockLevelTaskType(this), () -> CompatUtils.isPluginEnabled("FabledSkyBlock")); // not tested
            taskTypeManager.registerTaskType(() -> new PinataPartyHitTaskType(this), () -> CompatUtils.isPluginEnabled("PinataParty"));
            taskTypeManager.registerTaskType(() -> new PlaceholderAPIEvaluateTaskType(this), () -> CompatUtils.isPluginEnabled("PlaceholderAPI"));
            taskTypeManager.registerTaskType(() -> new PlayerPointsEarnTaskType(this), () -> CompatUtils.isPluginEnabled("PlayerPoints"));
            taskTypeManager.registerTaskType(() -> new PyroFishingProFishingTaskType(this), () -> CompatUtils.isPluginEnabled("PyroFishingPro") && CompatUtils.classExists("me.arsmagica.API.PyroFishCatchEvent"));
            taskTypeManager.registerTaskType(() -> new ServerNPCDeliverTaskType(this), () -> CompatUtils.isPluginEnabled("ServerNPC") && CompatUtils.classWithMethodExists("com.isnakebuzz.npcapi.entities.SnakeNPC", "getSettings"));
            taskTypeManager.registerTaskType(() -> new ServerNPCInteractTaskType(this), () -> CompatUtils.isPluginEnabled("ServerNPC") && CompatUtils.classWithMethodExists("com.isnakebuzz.npcapi.entities.SnakeNPC", "getSettings"));
            taskTypeManager.registerTaskType(() -> new ShopGUIPlusBuyTaskType(this), () -> CompatUtils.isPluginEnabled("ShopGUIPlus")); // not tested
            taskTypeManager.registerTaskType(() -> new ShopGUIPlusSellTaskType(this), () -> CompatUtils.isPluginEnabled("ShopGUIPlus")); // not tested
            taskTypeManager.registerTaskType(() -> new SuperiorSkyblockLevelType(this), () -> CompatUtils.isPluginEnabled("SuperiorSkyblock2")); // not tested
            taskTypeManager.registerTaskType(() -> new SuperiorSkyblockWorthType(this), () -> CompatUtils.isPluginEnabled("SuperiorSkyblock2")); // not tested
            taskTypeManager.registerTaskType(() -> new uSkyBlockLevelTaskType(this), () -> CompatUtils.isPluginEnabled("uSkyBlock"));
            taskTypeManager.registerTaskType(() -> new NuVotifierVoteTaskType(this), () -> CompatUtils.isPluginEnabled("Votifier")); // not tested
            taskTypeManager.registerTaskType(() -> new VotingPluginVoteType(this), () -> CompatUtils.isPluginEnabled("VotingPlugin")); // not tested
            taskTypeManager.registerTaskType(() -> new ZNPCsPlusDeliverTaskType(this), () -> CompatUtils.isPluginEnabled("ZNPCsPlus"));
            taskTypeManager.registerTaskType(() -> new ZNPCsPlusInteractTaskType(this), () -> CompatUtils.isPluginEnabled("ZNPCsPlus"));
            taskTypeManager.registerTaskType(() -> new BedWars1058BedBreakTask(this), () -> CompatUtils.isPluginEnabled("BedWars1058"));
            taskTypeManager.registerTaskType(() -> new BedWars1058FinalKillTask(this), () -> CompatUtils.isPluginEnabled("BedWars1058"));
            taskTypeManager.registerTaskType(() -> new BedWars1058WinTask(this), () -> CompatUtils.isPluginEnabled("BedWars1058"));
            taskTypeManager.registerTaskType(() -> new BedWars1058LoseTask(this), () -> CompatUtils.isPluginEnabled("BedWars1058"));

            // Register task types with enabled specific version plugin compatibility requirement
            taskTypeManager.registerTaskType(() -> new IridiumSkyblockValueTaskType(this), () -> { // TODO FIX
                String pluginVersion = CompatUtils.getPluginVersion("IridiumSkyblock");
                return pluginVersion != null && pluginVersion.startsWith("2");
            });
            taskTypeManager.registerTaskType(() -> new MythicMobsKillingTaskType(this), () -> {
                String pluginVersion = CompatUtils.getPluginVersion("MythicMobs");
                return pluginVersion != null && (pluginVersion.startsWith("4") || pluginVersion.startsWith("5"));
            });

            // Close task type registrations
            taskTypeManager.closeRegistrations();

            // Inform about registered task types
            final String registrationMessage = this.getRegistrationMessage();
            this.questsLogger.info(registrationMessage);

            if (playerBlockTrackerHook != null) {
                this.playerBlockTrackerHook.fixPlayerBlockTracker();
            }

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

    /**
     * Gets the server minor version.
     *
     * @return the server minor version
     * @throws IllegalArgumentException with message set to the bukkit version if it could not be parsed successfully.
     */
    private int getServerVersion() throws IllegalArgumentException {
        final String bukkitVersion = this.getServer().getBukkitVersion();

        final String[] bukkitVersionParts = bukkitVersion.split("\\.", 3);
        if (bukkitVersionParts.length < 2) {
            throw new IllegalArgumentException(bukkitVersion, new ArrayIndexOutOfBoundsException(bukkitVersionParts.length));
        }

        final String minorVersionPart = bukkitVersionParts[1].split("-")[0];
        try {
            return Integer.parseInt(minorVersionPart);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(bukkitVersion, e);
        }
    }

    /**
     * Gets the tasks registration message.
     *
     * @return the tasks registration message
     */
    private @NotNull String getRegistrationMessage() {
        final int registered = this.taskTypeManager.getRegistered();
        final int skipped = this.taskTypeManager.getSkipped();
        final int unsupported = Boolean.getBoolean("Quests.ShowUnsupportedCount") ? this.taskTypeManager.getUnsupported() : 0;

        final StringBuilder sb = new StringBuilder();
        sb.append(registered).append(" task types have been registered");

        if (skipped + unsupported > 0) {
            sb.append(' ').append('(');

            if (skipped > 0) {
                sb.append(skipped).append(" skipped due to exclusions or conflicting names");
            }

            if (skipped * unsupported > 0) {
                sb.append(',').append(' ');
            }

            if (unsupported > 0) {
                sb.append(unsupported).append(" not supported");
            }

            sb.append(')');
        }

        sb.append('.');
        return sb.toString();
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

        serverScheduler.cancelAllTasks();
    }

    @Override
    public void reloadQuests() {
        if (this.reloadBaseConfiguration(false)) {
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

    public @NotNull QuestItem getConfiguredQuestItem(final @NotNull String path, final @NotNull ConfigurationSection config, final @NotNull ItemGetter.Filter @NotNull ... excludes) {
        final String questItemId = config.getString(path + ".quest-item");

        if (questItemId != null) {
            final QuestItem questItem = this.questItemRegistry.getItem(questItemId);

            if (questItem != null) {
                return questItem;
            }
        }

        return new ParsedQuestItem("defined", null, this.getItemStack(path, config, excludes));
    }

    public @NotNull ItemStack getConfiguredItemStack(final @NotNull String path, final @NotNull ConfigurationSection config, final @NotNull ItemGetter.Filter @NotNull ... excludes) {
        final String questItemId = config.getString(path + ".quest-item");

        if (questItemId != null) {
            final QuestItem questItem = this.questItemRegistry.getItem(questItemId);

            if (questItem != null) {
                return questItem.getItemStack();
            }
        }

        return this.itemGetter.getItem(path, config, excludes);
    }

    public @NotNull ItemStack getItemStack(final @NotNull String path, final @NotNull ConfigurationSection config, final @NotNull ItemGetter.Filter @NotNull ... excludes) {
        return this.itemGetter.getItem(path, config, excludes);
    }

    private boolean reloadBaseConfiguration(final boolean initialLoad) {
        this.validConfiguration = this.questsConfig.loadConfig();

        if (this.validConfiguration) {
            final int loggingLevelNumber = this.questsConfig.getInt("options.verbose-logging-level", 2);
            final QuestsLogger.LoggingLevel loggingLevel = QuestsLogger.LoggingLevel.fromNumber(loggingLevelNumber);
            this.questsLogger.setServerLoggingLevel(loggingLevel);

            final boolean logHistoryEnabled = this.questsConfig.getBoolean("options.record-log-history", true);
            this.logHistory.setEnabled(logHistoryEnabled);

            //noinspection SwitchStatementWithTooFewBranches
            switch (this.questsConfig.getString("quest-mode.mode", "normal").toLowerCase()) {
                default:
                case "normal":
                    this.questController = new NormalQuestController(this);
                    // TODO the other one
            }

            // Don't do that on first load as the plugin will later call reloadQuests()
            // in the onEnable() lambda which calls this method and makes it try to cancel
            // task that has not been scheduled yet
            if (!initialLoad) {
                final long autoSaveInterval = this.getConfig().getLong("options.performance-tweaking.quest-autosave-interval", 12000);
                try {
                    if (this.questAutoSaveTask != null && !this.questAutoSaveTask.isCancelled()) {
                        this.questAutoSaveTask.cancel();
                    }
                    this.questAutoSaveTask = this.serverScheduler.runTaskTimer(new QuestsAutoSaveRunnable(this), autoSaveInterval, autoSaveInterval);
                } catch (final Exception e) {
                    this.getLogger().log(Level.SEVERE, "Cannot cancel and restart quest autosave task", e);
                }

                final long queueExecuteInterval = this.getConfig().getLong("options.performance-tweaking.quest-queue-executor-interval", 1);
                try {
                    if (this.questQueuePollTask != null && !this.questQueuePollTask.isCancelled()) {
                        this.questQueuePollTask.cancel();
                    }
                    this.questQueuePollTask = this.serverScheduler.runTaskTimer(this.questCompleter, queueExecuteInterval, queueExecuteInterval);
                } catch (final Exception e) {
                    this.getLogger().log(Level.SEVERE, "Could not cancel and restart queue executor task", e);
                }
            }

            // Set number formats to be used
            FormatUtils.setNumberFormats(this);
        }

        return this.validConfiguration;
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

    private void setTitleHandle() {
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            titleHandle = new Title_Bukkit();
            return;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            Player.class.getMethod("sendTitle", String.class, String.class);
            titleHandle = new Title_BukkitNoTimings();
            return;
        } catch (NoSuchMethodException ignored) {
        }

        titleHandle = new Title_Nothing();
    }

    private void setBossBarHandle() {
        try {
            Bukkit.class.getMethod("createBossBar", String.class, Class.forName("org.bukkit.boss.BarColor"), Class.forName("org.bukkit.boss.BarStyle"), Class.forName("[Lorg.bukkit.boss.BarFlag;"));
            bossBarHandle = new BossBar_Bukkit(this);
            return;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }

        bossBarHandle = new BossBar_Nothing();
    }

    private void setActionBarHandle() {
        try {
            Player.class.getMethod("sendActionBar", String.class);
            actionBarHandle = new ActionBar_Paper();
            return;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            Class.forName("org.bukkit.entity.Player.Spigot").getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
            actionBarHandle = new ActionBar_Spigot();
            return;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }

        actionBarHandle = new ActionBar_Nothing();
    }

    private void setItemGetter() {
        // Spigot 1.20.5+
        if (CompatUtils.classWithMethodExists("org.bukkit.inventory.meta.ItemMeta", "setEnchantmentGlintOverride", Boolean.class)) {
            itemGetter = new ItemGetter20(this);
            return;
        }

        // Spigot 1.14+
        if (CompatUtils.classWithMethodExists("org.bukkit.inventory.meta.ItemMeta", "setCustomModelData", Integer.class)) {
            itemGetter = new ItemGetter14(this);
            return;
        }

        // Spigot 1.13+
        if (CompatUtils.classWithMethodExists("org.bukkit.inventory.meta.ItemMeta", "getAttributeModifiers")) {
            itemGetter = new ItemGetter13(this);
            return;
        }

        // Spigot 1.8+
        itemGetter = new ItemGetter8(this);
    }

    private void setSkullGetter() {
        // Paper 1.12+
        if (CompatUtils.classExists("com.destroystokyo.paper.profile.PlayerProfile")) {
            skullGetter = new PaperSkullGetter(this);
            return;
        }

        if (CompatUtils.classWithMethodExists("{}.inventory.CraftMetaSkull", "setProfile", GameProfile.class)) {
            // Spigot 1.18.1+
            if (CompatUtils.classExists("org.bukkit.profile.PlayerProfile")) {
                skullGetter = new ModernSkullGetter(this);
                return;
            }

            // Spigot 1.15.1+
            skullGetter = new BukkitSkullGetter(this);
            return;
        }

        // Spigot 1.8+
        skullGetter = new LegacySkullGetter(this);
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

    public @Nullable AbstractCMIHook getCMIHook() {
        return cmiHook;
    }

    public @Nullable AbstractCoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    public @Nullable AbstractEssentialsHook getEssentialsHook() {
        return essentialsHook;
    }

    public @Nullable AbstractPlayerBlockTrackerHook getPlayerBlockTrackerHook() {
        return playerBlockTrackerHook;
    }

    public @NotNull AbstractVaultHook getVaultHook() {
        return this.vaultHook;
    }

    public @Nullable AbstractWildStackerHook getWildStackerHook() {
        return wildStackerHook;
    }

    public ItemGetter getItemGetter() {
        return itemGetter;
    }

    public SkullGetter getSkullGetter() {
        return skullGetter;
    }

    public QuestsTitle getTitleHandle() {
        return titleHandle;
    }

    public QuestsBossBar getBossBarHandle() {
        return bossBarHandle;
    }

    public QuestsActionBar getActionBarHandle() {
        return actionBarHandle;
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
        this.reloadBaseConfiguration(false);
    }
}
