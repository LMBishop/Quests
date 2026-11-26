package com.leonardobishop.quests.bukkit.tasktype.type.dealdamage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public final class DealDamageTaskType extends BukkitTaskType<DealDamageTask> {

    private final BukkitQuestsPlugin plugin;

    public DealDamageTaskType(BukkitQuestsPlugin plugin) {
        super("dealdamage", TaskUtils.TASK_ATTRIBUTION_STRING, "Deal a certain amount of damage.", DealDamageTask.SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = plugin.getVersionSpecificHandler().getDamager(event);
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Damageable damageable)) {
            return;
        }

        Entity directSource = plugin.getVersionSpecificHandler().getDirectSource(event);
        ItemStack bowItem = directSource != null ? plugin.getProjectile2ItemCache().getItem(directSource) : null;
        ItemStack item = bowItem != null ? bowItem : plugin.getVersionSpecificHandler().getItemInMainHand(player);
        ItemStack projectileItem = directSource != null ? plugin.getVersionSpecificHandler().getAbstractArrowItem(directSource) : null;

        // Clamp entity damage as getDamage() returns Float.MAX_VALUE for killing a parrot with a cookie
        // https://github.com/LMBishop/Quests/issues/753
        double finalDamage = event.getFinalDamage();
        double health = damageable.getHealth();
        double damage = Math.clamp(finalDamage, 0.0d, health);

        for (TaskUtils.PendingTask<DealDamageTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            DealDamageTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player damaged " + entity.getType() + " for " + damage, quest.getId(), task.getId(), player.getUniqueId());

            if (task.allowOnlyCreatures && !(entity instanceof Creature)) {
                super.debug(entity.getType() + " is not a creature but allow-only-creatures is true, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchEntity(this, pendingTask, task.mobs, entity, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.item != null) {
                if (item == null) {
                    super.debug("Specific item is required, player has no item in hand; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                super.debug("Specific item is required; player held item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                if (!task.item.compareItemStack(item, task.exactMatch)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
                }
            }

            if (task.projectileItem != null) {
                if (projectileItem == null) {
                    super.debug("Specific projectile item is required, no projectile item detected; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                super.debug("Specific projectile item is required; projectile item is of type '" + projectileItem.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                if (!task.projectileItem.compareItemStack(projectileItem, task.projectileItemExactMatch)) {
                    super.debug("Projectile item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Projectile item matches required item", quest.getId(), task.getId(), player.getUniqueId());
                }
            }

            double progress = Math.min(task.amount, TaskUtils.getDecimalTaskProgress(taskProgress) + damage);

            taskProgress.setProgress(progress);
            super.debug("Updating task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
