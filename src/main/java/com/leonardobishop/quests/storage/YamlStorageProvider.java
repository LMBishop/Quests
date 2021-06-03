package com.leonardobishop.quests.storage;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class YamlStorageProvider implements StorageProvider {

    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    private Quests plugin;

    public YamlStorageProvider(Quests plugin) {
        this.plugin = plugin;
    }

    private void lock(UUID uuid) {
        locks.putIfAbsent(uuid, new ReentrantLock());
        locks.get(uuid).lock();
    }

    private void unlock(UUID uuid) {
        ReentrantLock lock = locks.get(uuid);
        if (lock != null) lock.unlock();
    }

    public QuestProgressFile loadProgressFile(UUID uuid) {
        lock(uuid);
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
                            boolean completed = data.getBoolean("quest-progress." + id + ".completed");
                            boolean completedBefore = data.getBoolean("quest-progress." + id + ".completed-before");
                            long completionDate = data.getLong("quest-progress." + id + ".completion-date");

                            QuestProgress questProgress = new QuestProgress(plugin, id, completed, completedBefore, completionDate, uuid, started, true);

                            if (data.isConfigurationSection("quest-progress." + id + ".task-progress")) {
                                for (String taskid : data.getConfigurationSection("quest-progress." + id + ".task-progress").getKeys(false)) {
                                    boolean taskCompleted = data.getBoolean("quest-progress." + id + ".task-progress." + taskid + ".completed");
                                    Object taskProgression = data.get("quest-progress." + id + ".task-progress." + taskid + ".progress");

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
            plugin.getQuestsLogger().severe("Failed to load player: " + uuid + "! This WILL cause errors.");
            ex.printStackTrace();
            // fuck
        } finally {
            unlock(uuid);
        }

        return questProgressFile;
    }

    public void saveProgressFile(UUID uuid, QuestProgressFile questProgressFile) {
        lock(uuid);
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
            data.set("quest-progress", null);
            for (QuestProgress questProgress : questProgressValues) {
                data.set("quest-progress." + questProgress.getQuestId() + ".started", questProgress.isStarted());
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
                plugin.getQuestsLogger().debug("Write of player " + uuid + " to disk complete.");
            } catch (IOException e) {
                plugin.getQuestsLogger().debug("Failed to write player: " + uuid + "!.");
                e.printStackTrace();
            }
        } finally {
            unlock(uuid);
        }
    }
}
