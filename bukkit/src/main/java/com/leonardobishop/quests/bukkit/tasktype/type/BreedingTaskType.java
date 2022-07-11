package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.List;

public final class BreedingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BreedingTaskType(BukkitQuestsPlugin plugin) {
        super("breeding", TaskUtils.TASK_ATTRIBUTION_STRING, "Breed a set amount of animals.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useEntityListConfigValidator(this, "mob", "mobs"));
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
            if (current instanceof Player player && !current.hasMetadata("NPC")) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) {
                    continue;
                }

                for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
                    Quest quest = pendingTask.quest();
                    Task task = pendingTask.task();
                    TaskProgress taskProgress = pendingTask.taskProgress();

                    super.debug("Player detected near bred animal", quest.getId(), task.getId(), player.getUniqueId());

                    List<String> configEntities = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("mob") ? "mob" : "mobs");

                    if (!configEntities.isEmpty()) {
                        super.debug("List of required entities exists; mob type is " + current.getType(), quest.getId(), task.getId(), player.getUniqueId());

                        boolean validMob = false;
                        for (String entry : configEntities) {
                            super.debug("Checking against mob '" + entry + "'", quest.getId(), task.getId(), player.getUniqueId());
                            try {
                                EntityType entity = EntityType.valueOf(entry);
                                if (current.getType() == entity) {
                                    super.debug("Mob is valid", quest.getId(), task.getId(), player.getUniqueId());
                                    validMob = true;
                                    break;
                                }
                            } catch (IllegalArgumentException ignored) {
                            }
                        }

                        if (!validMob) {
                            super.debug("Mob is not in list of required mobs, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                            continue;
                        }
                    }


                    int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                    super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

                    int breedingNeeded = (int) task.getConfigValue("amount");

                    if (progress >= breedingNeeded) {
                        super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }
}
