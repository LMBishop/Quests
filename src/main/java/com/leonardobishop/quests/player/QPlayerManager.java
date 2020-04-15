package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QPlayerManager {

    private final Quests plugin;

    public QPlayerManager(Quests plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, QPlayer> qPlayers = new HashMap<>();

    /**
     * Gets the QPlayer from a given UUID.
     * Calls {@link QPlayerManager#getPlayer(UUID, boolean)} with the 2nd argument as false.
     *
     * @param uuid the uuid
     * @return {@link QPlayer} if they are loaded
     */
    public QPlayer getPlayer(UUID uuid) {
        return getPlayer(uuid, false);
    }

    /**
     * Gets the QPlayer from a given UUID.
     *
     * @param uuid the uuid
     * @param loadIfNull load the QPlayer if the result is null and return the QPlayer if successfully loaded
     * @return {@link QPlayer} if they are loaded
     */
    public QPlayer getPlayer(UUID uuid, boolean loadIfNull) {
        QPlayer qPlayer = qPlayers.get(uuid);
        if (qPlayer == null && loadIfNull) {
            plugin.getQuestsLogger().debug("QPlayer of " + uuid + " is null, but was requested! Attempting to load it.");
            loadPlayer(uuid, false);
            return getPlayer(uuid, false);
        }
        return qPlayers.get(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.getPlayer(uuid).getQuestProgressFile().saveToDisk(false);
        qPlayers.remove(uuid);
    }

    public Collection<QPlayer> getQPlayers() {
        return qPlayers.values();
    }

    //Better to use the second method, so we know if we only load the data
    //public void loadPlayer(UUID uuid) {
    //   loadPlayer(uuid, false);
    //}

    // TODO redo "onlyData" and use a less confusing way
    public void loadPlayer(UUID uuid, boolean onlyData) {
        if (getPlayer(uuid) == null || getPlayer(uuid).isOnlyDataLoaded()) {
            QuestProgressFile questProgressFile = new QuestProgressFile(uuid, plugin);

            try {
                File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
                if (directory.exists() && directory.isDirectory()) {
                    File file = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
                    if (file.exists()) {
                        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                        if (data.isConfigurationSection("quest-progress")) { //Same job as "isSet" + it checks if is CfgSection
                            for (String id : data.getConfigurationSection("quest-progress").getKeys(false)) {
                                boolean started = data.getBoolean("quest-progress." + id + ".started");
                                boolean completed = data.getBoolean("quest-progress." + id + ".completed");
                                boolean completedBefore = data.getBoolean("quest-progress." + id + ".completed-before");
                                long completionDate = data.getLong("quest-progress." + id + ".completion-date");

                                QuestProgress questProgress = new QuestProgress(id, completed, completedBefore, completionDate, uuid, started, true);

                                for (String taskid : data.getConfigurationSection("quest-progress." + id + ".task-progress").getKeys(false)) {
                                    boolean taskCompleted = data.getBoolean("quest-progress." + id + ".task-progress." + taskid + ".completed");
                                    Object taskProgression = data.get("quest-progress." + id + ".task-progress." + taskid + ".progress");

                                    TaskProgress taskProgress = new TaskProgress(taskid, taskProgression, uuid, taskCompleted, false);
                                    questProgress.addTaskProgress(taskProgress);
                                }

                                questProgressFile.addQuestProgress(questProgress);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                plugin.getQuestsLogger().severe("Failed to load player: " + uuid + "! This WILL cause errors.");
                ex.printStackTrace();
                // fuck
            }

            QPlayer qPlayer = new QPlayer(uuid, questProgressFile, onlyData, plugin);

            this.qPlayers.put(uuid, qPlayer);
        }
    }
}
