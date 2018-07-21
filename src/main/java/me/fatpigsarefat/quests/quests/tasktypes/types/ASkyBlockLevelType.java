package me.fatpigsarefat.quests.quests.tasktypes.types;

import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;
import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import me.fatpigsarefat.quests.quests.tasktypes.ConfigValue;
import me.fatpigsarefat.quests.quests.tasktypes.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public final class ASkyBlockLevelType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public ASkyBlockLevelType() {
        super("askyblock_level", "fatpigsarefat", "Reach a certain island level for ASkyBlock.");
        this.creatorConfigValues.add(new ConfigValue("level", true, "Minimum island level needed."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandPostLevelEvent event) {
        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(event.getPlayer());
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

                    long islandLevelNeeded = (long) (int) task.getConfigValue("level");

                    taskProgress.setProgress(event.getLongLevel());

                    if (((long) taskProgress.getProgress()) >= islandLevelNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }
}
