package com.leonardobishop.quests.player;

import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.Quests;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class QPlayerManager {

    private final Quests plugin;

    public QPlayerManager(Quests plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, QPlayer> qPlayers = new HashMap<>();

    public void addPlayer(QPlayer qPlayer) {
        qPlayers.put(qPlayer.getUuid(), qPlayer);
    }

    public QPlayer getPlayer(UUID uuid) {
        return qPlayers.get(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.getPlayer(uuid).getQuestProgressFile().saveToDisk(false); //WHY THE HECK THE DATA WAS NOT SAVED???
        qPlayers.remove(uuid);
    }

    public Collection<QPlayer> getQPlayers() {
        return qPlayers.values();
    }

    public void loadPlayer(UUID uuid) {
        loadPlayer(uuid, false);
    }

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
                plugin.getLogger().severe("Failed to load player: " + uuid + "! This WILL cause errors.");
                ex.printStackTrace();
                // fuck
            }

            QPlayer qPlayer = new QPlayer(uuid, questProgressFile, onlyData, plugin);

            addPlayer(qPlayer);
        }
    }

}
