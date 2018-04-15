package me.fatpigsarefat.quests.quests.tasktypes;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import us.talabrek.ultimateskyblock.api.event.uSkyBlockScoreChangedEvent;

public final class uSkyBlockLevelType extends TaskType {

    public uSkyBlockLevelType() {
        super("uskyblock_level", "fatpigsarefat", "Reach a certain island level for uSkyBlock.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(uSkyBlockScoreChangedEvent event) {
        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    double islandLevelNeeded = (double) (int) task.getConfigValue("level");

                    taskProgress.setProgress(event.getScore().getScore());

                    if (((double) taskProgress.getProgress()) >= islandLevelNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
