package me.fatpigsarefat.quests.player;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QPlayerManager {

    private List<QPlayer> qPlayers = new ArrayList<>();

    public void addPlayer(QPlayer qPlayer) {
        qPlayers.add(qPlayer);
    }

    public QPlayer getPlayer(UUID uuid) {
        for (QPlayer qPlayer : qPlayers) {
            if (qPlayer.getUuid().equals(uuid)) {
                return qPlayer;
            }
        }
        return null;
    }

    public void removePlayer(UUID uuid) {
        QPlayer toRemove = null;
        for (QPlayer qPlayer : qPlayers) {
            if (qPlayer.getUuid().equals(uuid)) {
                toRemove = qPlayer;
                break;
            }
        }
        if (toRemove != null) {
            qPlayers.remove(toRemove);
        }
    }

    public List<QPlayer> getQPlayers() {
        return qPlayers;
    }

    public void loadPlayer(UUID uuid) {
        if (getPlayer(uuid) == null) {
            QuestProgressFile questProgressFile = new QuestProgressFile(uuid);

            File directory = new File(Quests.getInstance().getDataFolder() + File.separator + "playerdata");
            if (directory.exists() && directory.isDirectory()) {
                File file = new File(Quests.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
                if (file.exists()) {
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                    if (data.contains("quest-progress")) {
                        for (String id : data.getConfigurationSection("quest-progress").getKeys(false)) {
                            boolean started = data.getBoolean("quest-progress." + id + ".started");
                            boolean completed = data.getBoolean("quest-progress." + id + ".completed");
                            boolean completedBefore = data.getBoolean("quest-progress." + id + ".completed-before");
                            long completionDate = data.getLong("quest-progress." + id + ".completion-date");

                            QuestProgress questProgress = new QuestProgress(id, completed, completedBefore, completionDate, uuid, started, true);

                            for (String taskid : data.getConfigurationSection("quest-progress." + id + ".task-progress").getKeys(false)) {
                                boolean taskCompleted = data.getBoolean("quest-progress." + id + ".task-progress." + taskid + ".completed");
                                Object taskProgression = data.get("quest-progress." + id + ".task-progress." + taskid + ".progress");

                                TaskProgress taskProgress = new TaskProgress(taskid, taskProgression, uuid, taskCompleted);
                                questProgress.addTaskProgress(taskProgress);
                            }

                            questProgressFile.addQuestProgress(questProgress);
                        }
                    }
                }
            }

            QPlayer qPlayer = new QPlayer(uuid, questProgressFile);

            Quests.getPlayerManager().addPlayer(qPlayer);
        }
    }

}
