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
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;

public final class MilkingTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MilkingTaskType() {
        super("milking", "fatpigsarefat", "Milk a set amount of cows.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of cows to be milked."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMilk(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Cow) || (event.getPlayer().getItemInHand().getType() != Material.BUCKET)) {
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

                    int cowsNeeded = (int) task.getConfigValue("amount");

                    int progressMilked;
                    if (taskProgress.getProgress() == null) {
                        progressMilked = 0;
                    } else {
                        progressMilked = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressMilked + 1);

                    if (((int) taskProgress.getProgress()) >= cowsNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
