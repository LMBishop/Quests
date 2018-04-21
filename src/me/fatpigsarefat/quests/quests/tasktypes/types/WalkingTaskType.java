package me.fatpigsarefat.quests.quests.tasktypes.types;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import me.fatpigsarefat.quests.quests.tasktypes.ConfigValue;
import me.fatpigsarefat.quests.quests.tasktypes.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public final class WalkingTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public WalkingTaskType() {
        super("walking", "fatpigsarefat", "Walk a set distance.");
        this.creatorConfigValues.add(new ConfigValue("distance", true, "Amount of meters (blocks) to be travelled."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
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

                    int distanceNeeded = (int) task.getConfigValue("distance");

                    int progressDistance;
                    if (taskProgress.getProgress() == null) {
                        progressDistance = 0;
                    } else {
                        progressDistance = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressDistance + 1);

                    if (((int) taskProgress.getProgress()) >= distanceNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
