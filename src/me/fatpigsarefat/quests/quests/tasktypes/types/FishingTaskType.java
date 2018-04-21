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
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;

public final class FishingTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public FishingTaskType() {
        super("fishing", "fatpigsarefat", "Catch a set amount of items from the sea.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of fish to be caught."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
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
