package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTransformEvent;

public final class CuringTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CuringTaskType(BukkitQuestsPlugin plugin) {
        super("curing", TaskUtils.TASK_ATTRIBUTION_STRING, "Cure a set amount of zombie villagers.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event) {
        EntityTransformEvent.TransformReason reason = event.getTransformReason();
        if (reason != EntityTransformEvent.TransformReason.CURED) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof ZombieVillager zombieVillager)) {
            return;
        }

        OfflinePlayer offlinePlayer = zombieVillager.getConversionPlayer();
        if (!(offlinePlayer instanceof Player player)) {
            return;
        }

        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Villager.Type type = zombieVillager.getVillagerType();
        Villager.Profession profession = zombieVillager.getVillagerProfession();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            // I don't know why my IDE thinks profession
            // is always null, probably a bad API design.
            //
            // Yeah, ZombieVillager is missing an override
            // for annotation from the Zombie interface.
            //
            //noinspection ConstantValue
            super.debug("Player cured " + zombieVillager.getType() + " of profession " + profession + " and type " + type, quest.getId(), task.getId(), player.getUniqueId());

            // TODO: add villager-type and villager-profession options

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
