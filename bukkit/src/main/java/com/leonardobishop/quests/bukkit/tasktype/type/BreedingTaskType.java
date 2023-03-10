package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public final class BreedingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BreedingTaskType(BukkitQuestsPlugin plugin) {
        super("breeding", TaskUtils.TASK_ATTRIBUTION_STRING, "Breed a set amount of animals.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useEntityListConfigValidator(this, "mob", "mobs"));

        try {
            Class.forName("org.bukkit.event.entity.EntityBreedEvent");
            plugin.getServer().getPluginManager().registerEvents(new BreedingTaskType.EntityBreedListener(), plugin);
        } catch (ClassNotFoundException ignored) {
            // server version cannot support the event, so we use CreatureSpawnEvent instead
            plugin.getServer().getPluginManager().registerEvents(new BreedingTaskType.CreatureSpawnListener(), plugin);
        }
    }

    private final class EntityBreedListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onEntityBreed(org.bukkit.event.entity.EntityBreedEvent event) {
            LivingEntity breeder = event.getBreeder();
            if (breeder instanceof Player player) {
                handle(player, event.getEntityType());
            }
        }
    }

    private final class CreatureSpawnListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) {
                return;
            }

            List<Entity> entities = event.getEntity().getNearbyEntities(10.0d, 10.0d, 10.0d);
            for (Entity entity : entities) {
                if (entity instanceof Player player) {
                    handle(player, event.getEntityType());
                }
            }
        }
    }

    private void handle(Player player, EntityType entityType) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player detected near bred animal", quest.getId(), task.getId(), player.getUniqueId());

            List<String> configEntities = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("mob") ? "mob" : "mobs");
            if (!configEntities.isEmpty()) {
                super.debug("List of required entities exists; mob type is " + entityType.name(), quest.getId(), task.getId(), player.getUniqueId());

                boolean validMob = false;
                for (String configEntity : configEntities) {
                    super.debug("Checking against mob '" + configEntity + "'", quest.getId(), task.getId(), player.getUniqueId());
                    try {
                        EntityType configEntityType = EntityType.valueOf(configEntity);
                        if (configEntityType == entityType) {
                            super.debug("Mob is valid", quest.getId(), task.getId(), player.getUniqueId());
                            validMob = true;
                            break;
                        }
                    } catch (IllegalArgumentException ignored) {}
                }

                if (!validMob) {
                    super.debug("Mob is not in list of required mobs, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");
            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }
}
