package me.fatpigsarefat.quests.quests.tasktypes;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public final class FishingTaskType extends TaskType {

    public FishingTaskType() {
        super("fishing", "fatpigsarefat", "Catch a set amount of items from the sea.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishCaught(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
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

                    int catchesNeeded = (int) task.getConfigValue("amount");

                    int progressCatches;
                    if (taskProgress.getProgress() == null) {
                        progressCatches = 0;
                    } else {
                        progressCatches = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressCatches + 1);

                    if (((int) taskProgress.getProgress()) >= catchesNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
