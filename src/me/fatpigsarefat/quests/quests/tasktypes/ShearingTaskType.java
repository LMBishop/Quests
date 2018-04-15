package me.fatpigsarefat.quests.quests.tasktypes;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

public final class ShearingTaskType extends TaskType {

    public ShearingTaskType() {
        super("shearing", "fatpigsarefat", "Shear a set amount of sheep.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if (!(event.getEntity() instanceof Sheep)) {
            return;
        }

        Player player = event.getPlayer();

        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int sheepNeeded = (int) task.getConfigValue("amount");

                    int progressSheared;
                    if (taskProgress.getProgress() == null) {
                        progressSheared = 0;
                    } else {
                        progressSheared = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressSheared + 1);

                    if (((int) taskProgress.getProgress()) >= sheepNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
