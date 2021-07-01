package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BreedingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BreedingTaskType(BukkitQuestsPlugin plugin) {
        super("breeding", TaskUtils.TASK_ATTRIBUTION_STRING, "Breed a set amount of animals.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(CreatureSpawnEvent e) {
        if (!e.getSpawnReason().equals(SpawnReason.BREEDING)) {
            return;
        }

        Entity ent = e.getEntity();
        List<Entity> entList = ent.getNearbyEntities(10, 10, 10);

        if (entList.isEmpty()) {
            return;
        }
        // Check if there is a player in the list, otherwise: return.
        for (Entity current : entList) {
            if (current instanceof Player && !current.hasMetadata("NPC")) {
                Player player = (Player) current;
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) {
                    continue;
                }

                for (Quest quest : super.getRegisteredQuests()) {
                    if (qPlayer.hasStartedQuest(quest)) {
                        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                        for (Task task : quest.getTasksOfType(super.getType())) {
                            if (!TaskUtils.validateWorld(player, task)) continue;

                            TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                            if (taskProgress.isCompleted()) {
                                continue;
                            }

                            int breedingNeeded = (int) task.getConfigValue("amount");
                            int breedingProgress;

                            if (taskProgress.getProgress() == null) {
                                breedingProgress = 0;
                            } else {
                                breedingProgress = (int) taskProgress.getProgress();
                            }

                            taskProgress.setProgress(breedingProgress + 1);

                            if (((int) taskProgress.getProgress()) >= breedingNeeded) {
                                taskProgress.setCompleted(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
