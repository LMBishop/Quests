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
import org.bukkit.event.entity.EntityTameEvent;

public final class TamingTaskType extends TaskType {

    public TamingTaskType() {
        super("taming", "fatpigsarefat", "Tame a set amount of animals.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getOwner();

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

                    int tamesNeeded = (int) task.getConfigValue("amount");

                    int progressTamed;
                    if (taskProgress.getProgress() == null) {
                        progressTamed = 0;
                    } else {
                        progressTamed = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressTamed + 1);

                    if (((int) taskProgress.getProgress()) >= tamesNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
