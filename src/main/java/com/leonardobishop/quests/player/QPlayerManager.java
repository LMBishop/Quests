package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsLogger;
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
     * @param loadIfNull do not use
     * @return {@link QPlayer} if they are loaded
     */
    public QPlayer getPlayer(UUID uuid, boolean loadIfNull) {
        QPlayer qPlayer = qPlayers.get(uuid);
        if (qPlayer == null) {
            plugin.getQuestsLogger().debug("QPlayer of " + uuid + " is null, but was requested:");
            if (plugin.getQuestsLogger().getServerLoggingLevel() == QuestsLogger.LoggingLevel.DEBUG) {
                Thread.dumpStack();
            }
        }
        return qPlayer;
    }

    public void removePlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + ".");
        this.getPlayer(uuid).getQuestProgressFile().saveToDisk(false);
        qPlayers.remove(uuid);
    }

    public void dropPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Dropping player " + uuid + ".");
        qPlayers.remove(uuid);
    }

    public Collection<QPlayer> getQPlayers() {
        return qPlayers.values();
    }

    //Better to use the second method, so we know if we only load the data
    //public void loadPlayer(UUID uuid) {
    //   loadPlayer(uuid, false);
    //}

    public void loadPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Loading player " + uuid + " from disk.");
        if (qPlayers.get(uuid) == null) {
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
            }

            QPlayer qPlayer = new QPlayer(uuid, questProgressFile, plugin);

            this.qPlayers.put(uuid, qPlayer);
        } else {
            plugin.getQuestsLogger().debug("Player " + uuid + " is already loaded.");
        }
    }
}
