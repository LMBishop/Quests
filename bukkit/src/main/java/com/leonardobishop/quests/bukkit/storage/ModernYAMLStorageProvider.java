package com.leonardobishop.quests.bukkit.storage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayerData;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.storage.StorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public final class ModernYAMLStorageProvider implements StorageProvider {

    private final BukkitQuestsPlugin plugin;
    private final File dataDirectory;
    private final Map<UUID, ReentrantLock> lockMap;

    private boolean validateQuests;

    public ModernYAMLStorageProvider(final @NotNull BukkitQuestsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.dataDirectory = new File(plugin.getDataFolder(), "playerdata");
        this.lockMap = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull String getName() {
        return "yaml";
    }

    @Override
    public void init() {
        //noinspection ResultOfMethodCallIgnored
        this.dataDirectory.mkdirs();

        // not really useful now, but maybe in the future it will be reloadable
        this.validateQuests = this.plugin.getConfig().getBoolean("options.verify-quest-exists-on-load", true);
    }

    @Override
    public void shutdown() {
        // no implementation needed
    }

    @Override
    public @Nullable QPlayerData loadPlayerData(final @NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        final String uuidString = uuid.toString();
        final QuestProgressFile questProgressFile = new QuestProgressFile(this.plugin, uuid);
        final File dataFile = new File(this.dataDirectory, uuidString + ".yml");

        final ReentrantLock lock = this.lock(uuid);

        try {
            if (dataFile.isFile()) {
                final YamlConfiguration data = new YamlConfiguration();
                data.load(dataFile);

                this.plugin.getQuestsLogger().debug("Player " + uuidString + " has a valid quest progress file.");

                final ConfigurationSection questProgressSection = data.getConfigurationSection("quest-progress");

                if (questProgressSection != null) {
                    final Set<String> questIds = questProgressSection.getKeys(false);

                    for (final String questId : questIds) {
                        final Quest quest;

                        if (this.validateQuests) {
                            quest = this.plugin.getQuestManager().getQuestById(questId);

                            if (quest == null) {
                                continue;
                            }
                        } else {
                            quest = null;
                        }

                        final ConfigurationSection questSection = questProgressSection.getConfigurationSection(questId);

                        //noinspection DataFlowIssue
                        final boolean qStarted = questSection.getBoolean("started", false);
                        final long qStartedDate = questSection.getLong("started-date", 0L);
                        final boolean qCompleted = questSection.getBoolean("completed", false);
                        final boolean qCompletedBefore = questSection.getBoolean("completed-before", false);
                        final long qCompletionDate = questSection.getLong("completion-date", 0L);

                        final QuestProgress questProgress = new QuestProgress(this.plugin, questId, uuid, qStarted, qStartedDate, qCompleted, qCompletedBefore, qCompletionDate);

                        final ConfigurationSection taskProgressSection = questSection.getConfigurationSection("task-progress");

                        if (taskProgressSection != null) {
                            final Set<String> taskIds = taskProgressSection.getKeys(false);

                            for (final String taskId : taskIds) {
                                // quest is not null only if this.validateQuests is true
                                if (quest != null) {
                                    final Task task = quest.getTaskById(taskId);

                                    if (task == null) {
                                        continue;
                                    }
                                }

                                final ConfigurationSection taskSection = taskProgressSection.getConfigurationSection(taskId);

                                //noinspection DataFlowIssue
                                final boolean tCompleted = taskSection.getBoolean("completed", false);
                                final Object tProgress = taskSection.get("progress", null);

                                final TaskProgress taskProgress = new TaskProgress(questProgress, taskId, uuid, tProgress, tCompleted);
                                questProgress.addTaskProgress(taskProgress);
                            }
                        }

                        questProgressFile.addQuestProgress(questProgress);
                    }
                }
            } else {
                this.plugin.getQuestsLogger().debug("Player " + uuidString + " does not have a quest progress file.");
            }

            return new QPlayerData(uuid, new QPlayerPreferences(null), questProgressFile); // TODO player preferences
        } catch (final FileNotFoundException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to find player data file for " + uuidString + ".", e);
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to read player data file for " + uuidString + ".", e);
        } catch (final InvalidConfigurationException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to parse player data file for " + uuidString + ".", e);
        } finally {
            lock.unlock();
        }

        return null;
    }

    @Override
    public boolean savePlayerData(final @NotNull QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        final UUID uuid = playerData.playerUUID();
        final String uuidString = uuid.toString();
        final QuestProgressFile questProgressFile = playerData.questProgressFile();

        final ReentrantLock lock = this.lock(uuid);

        try {
            final File dataFile = new File(this.dataDirectory, uuidString + ".yml");
            final YamlConfiguration data = new YamlConfiguration();

            if (dataFile.isFile()) {
                data.load(dataFile);
                this.plugin.getQuestsLogger().debug("Player " + uuidString + " has a valid quest progress file.");
            } else {
                this.plugin.getQuestsLogger().debug("Player " + uuidString + " does not have a quest progress file.");
            }

            for (final QuestProgress questProgress : questProgressFile.getAllQuestProgress()) {
                if (!questProgress.isModified()) {
                    continue;
                }

                final String questId = questProgress.getQuestId();

                data.set("quest-progress." + questId + ".started", questProgress.isStarted());
                data.set("quest-progress." + questId + ".started-date", questProgress.getStartedDate());
                data.set("quest-progress." + questId + ".completed", questProgress.isCompleted());
                data.set("quest-progress." + questId + ".completed-before", questProgress.isCompletedBefore());
                data.set("quest-progress." + questId + ".completion-date", questProgress.getCompletionDate());

                for (final TaskProgress taskProgress : questProgress.getTaskProgresses()) {
                    final String taskId = taskProgress.getTaskId();

                    data.set("quest-progress." + questId + ".task-progress." + taskId + ".completed", taskProgress.isCompleted());
                    data.set("quest-progress." + questId + ".task-progress." + taskId + ".progress", taskProgress.getProgress());
                }
            }

            this.plugin.getQuestsLogger().debug("Saving player data file for " + uuidString + " to disk.");

            try {
                data.save(dataFile);
                return true;
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to write player data file for " + uuidString + ".", e);
            }
        } catch (final FileNotFoundException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to find player data file for " + uuidString + ".", e);
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to read player data file for " + uuidString + ".", e);
        } catch (final InvalidConfigurationException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to parse player data file for " + uuidString + ".", e);
        } finally {
            lock.unlock();
        }

        return false;
    }

    @Override
    public @NotNull List<QPlayerData> loadAllPlayerData() {
        final List<QPlayerData> allPlayerData = new ArrayList<>();
        final PlayerDataVisitor playerDataVisitor = new PlayerDataVisitor(this, allPlayerData);

        try {
            Files.walkFileTree(this.dataDirectory.toPath(), playerDataVisitor);
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to walk the player data file tree", e);
        }

        return allPlayerData;
    }

    @Override
    public boolean isSimilar(final @NotNull StorageProvider otherProvider) {
        return otherProvider instanceof ModernYAMLStorageProvider;
    }

    private @NotNull ReentrantLock lock(final @NotNull UUID uuid) {
        final ReentrantLock lock = this.lockMap.computeIfAbsent(uuid, k -> new ReentrantLock());
        lock.lock();
        return lock;
    }

    private static class PlayerDataVisitor extends SimpleFileVisitor<Path> {

        private static final String FILE_EXTENSION = ".yml";

        private final ModernYAMLStorageProvider provider;
        private final List<QPlayerData> allPlayerData;

        public PlayerDataVisitor(final @NotNull ModernYAMLStorageProvider provider, final @NotNull List<QPlayerData> allPlayerData) {
            this.provider = provider;
            this.allPlayerData = allPlayerData;
        }

        @Override
        public @NotNull FileVisitResult visitFile(final @NotNull Path path, final @NotNull BasicFileAttributes attributes) {
            final String fileName = path.toFile().getName();
            final String uuidString = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());

            if (fileName.endsWith(FILE_EXTENSION)) {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(uuidString);
                } catch (final IllegalArgumentException e) {
                    this.provider.plugin.getLogger().log(Level.SEVERE, "Failed to parse player UUID: '" + uuidString + "'.", e);
                    return FileVisitResult.CONTINUE;
                }

                final QPlayerData playerData = this.provider.loadPlayerData(uuid);
                if (playerData != null) {
                    this.allPlayerData.add(playerData);
                }
            }

            return FileVisitResult.CONTINUE;
        }
    }
}
