package com.leonardobishop.quests.bukkit.storage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.storage.StorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class YamlStorageProvider implements StorageProvider {

    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final BukkitQuestsPlugin plugin;

    public YamlStorageProvider(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    private ReentrantLock lock(UUID uuid) {
        locks.putIfAbsent(uuid, new ReentrantLock());
        ReentrantLock lock = locks.get(uuid);
        lock.lock();
        return lock;
    }

    @Override
    public String getName() {
        return "yaml";
    }

    @Override
    public void init() {
        File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
        directory.mkdirs();
    }

    @Override
    public void shutdown() {
        // no impl
    }

    public @Nullable QuestProgressFile loadProgressFile(@NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        ReentrantLock lock = lock(uuid);
        Map<String, Quest> presentQuests = new HashMap<>(plugin.getQuestManager().getQuests());
        boolean validateQuests = plugin.getQuestsConfig().getBoolean("options.verify-quest-exists-on-load", true);
        
        QuestProgressFile questProgressFile = new QuestProgressFile(uuid, plugin);
        try {
            File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
            if (directory.exists() && directory.isDirectory()) {
                File file = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
                if (file.exists()) {
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                    plugin.getQuestsLogger().debug("Player " + uuid + " has a valid quest progress file.");
                    if (data.isConfigurationSection("quest-progress")) { //Same job as "isSet" + it checks if is CfgSection
                        for (String id : data.getConfigurationSection("quest-progress").getKeys(false)) {
                            boolean started = data.getBoolean("quest-progress." + id + ".started");
                            long startedDate = data.getLong("quest-progress." + id + ".started-date");
                            boolean completed = data.getBoolean("quest-progress." + id + ".completed");
                            boolean completedBefore = data.getBoolean("quest-progress." + id + ".completed-before");
                            long completionDate = data.getLong("quest-progress." + id + ".completion-date");

                            if (validateQuests && !presentQuests.containsKey(id)) continue;

                            QuestProgress questProgress = new QuestProgress(plugin, id, completed, completedBefore, completionDate, uuid, started, startedDate, true);

                            if (data.isConfigurationSection("quest-progress." + id + ".task-progress")) {
                                for (String taskid : data.getConfigurationSection("quest-progress." + id + ".task-progress").getKeys(false)) {
                                    boolean taskCompleted = data.getBoolean("quest-progress." + id + ".task-progress." + taskid + ".completed");
                                    Object taskProgression = data.get("quest-progress." + id + ".task-progress." + taskid + ".progress");

                                    if (validateQuests && presentQuests.get(id).getTaskById(taskid) == null) continue;

                                    TaskProgress taskProgress = new TaskProgress(questProgress, taskid, taskProgression, uuid, taskCompleted, false);
                                    questProgress.addTaskProgress(taskProgress);
                                }
                            }

                            questProgressFile.addQuestProgress(questProgress);
                        }
                    }
                } else {
                    plugin.getQuestsLogger().debug("Player " + uuid + " does not have a quest progress file.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }

        return questProgressFile;
    }

    public boolean saveProgressFile(@NotNull UUID uuid, @NotNull QuestProgressFile questProgressFile) {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        Objects.requireNonNull(questProgressFile, "questProgressFile cannot be null");

        ReentrantLock lock = lock(uuid);
        try {
            List<QuestProgress> questProgressValues = new ArrayList<>(questProgressFile.getAllQuestProgress());
            File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }

            File file = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            for (QuestProgress questProgress : questProgressValues) {
                if (!questProgress.isModified()) continue;
                data.set("quest-progress." + questProgress.getQuestId() + ".started", questProgress.isStarted());
                data.set("quest-progress." + questProgress.getQuestId() + ".started-date", questProgress.getStartedDate());
                data.set("quest-progress." + questProgress.getQuestId() + ".completed", questProgress.isCompleted());
                data.set("quest-progress." + questProgress.getQuestId() + ".completed-before", questProgress.isCompletedBefore());
                data.set("quest-progress." + questProgress.getQuestId() + ".completion-date", questProgress.getCompletionDate());
                for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                    data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".completed", taskProgress
                            .isCompleted());
                    data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".progress", taskProgress
                            .getProgress());
                }
            }

            plugin.getQuestsLogger().debug("Writing player " + uuid + " to disk.");
            try {
                data.save(file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public @NotNull List<QuestProgressFile> loadAllProgressFiles() {
        List<QuestProgressFile> files = new ArrayList<>();

        File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                if (path.toString().endsWith(".yml")) {
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(path.getFileName().toString().replace(".yml", ""));
                    } catch (IllegalArgumentException e) {
                        return FileVisitResult.CONTINUE;
                    }

                    QuestProgressFile file = loadProgressFile(uuid);
                    if (file != null) {
                        files.add(file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(directory.toPath(), fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    @Override
    public void saveAllProgressFiles(List<QuestProgressFile> files) {
        for (QuestProgressFile file : files) {
            saveProgressFile(file.getPlayerUUID(), file);
        }
    }

    @Override
    public boolean isSimilar(StorageProvider provider) {
        return provider instanceof YamlStorageProvider;
    }
}
