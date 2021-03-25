package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsLogger;
import com.leonardobishop.quests.player.questprogressfile.QPlayerPreferences;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QPlayerManager {

    private final Quests plugin;

    public QPlayerManager(Quests plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, QPlayer> qPlayers = new ConcurrentHashMap<>();

    /**
     * Gets the QPlayer from a given UUID.
     *
     * @param uuid the uuid
     * @return {@link QPlayer} if they are loaded
     */
    public QPlayer getPlayer(UUID uuid) {
        QPlayer qPlayer = qPlayers.get(uuid);
        if (qPlayer == null) {
            plugin.getQuestsLogger().debug("QPlayer of " + uuid + " is null, but was requested:");
            if (plugin.getQuestsLogger().getServerLoggingLevel() == QuestsLogger.LoggingLevel.DEBUG) {
                Thread.dumpStack();
            }
        }
        return qPlayer;
    }

    /**
     * Unloads and saves the player to disk. Must be invoked from the main thread.
     *
     * @param uuid the uuid of the player
     */
    public void removePlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + ".");
        this.getPlayer(uuid).getQuestProgressFile().saveToDisk(Options.QUEST_LEAVE_ASYNC.getBooleanValue());
        qPlayers.remove(uuid);
    }

    /**
     * Unloads the player without saving to disk.
     *
     * @param uuid the uuid of the player
     */
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

    /**
     * Load the player from disk if they exist, otherwise create a new {@link QuestProgressFile}.
     * This will have no effect if player is already loaded. Can be invoked asynchronously.
     *
     * @param uuid the uuid of the player
     */
    public void loadPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Loading player " + uuid + " from disk. Main thread: " + Bukkit.isPrimaryThread());
        qPlayers.computeIfAbsent(uuid, s -> {
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
            }

            return new QPlayer(uuid, questProgressFile, new QPlayerPreferences(null), plugin);
        });
//        else {
//            plugin.getQuestsLogger().debug("Player " + uuid + " is already loaded.");
//        }
    }
}
