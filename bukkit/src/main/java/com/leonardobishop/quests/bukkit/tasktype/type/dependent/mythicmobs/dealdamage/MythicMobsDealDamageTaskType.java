package com.leonardobishop.quests.bukkit.tasktype.type.dependent.mythicmobs.dealdamage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

@NullMarked
public final class MythicMobsDealDamageTaskType extends BukkitTaskType<MythicMobsDealDamageTask> {

    private final BukkitQuestsPlugin plugin;
    private final BiConsumer<Entity, BiConsumer<String, Double>> entityConsumer;

    public MythicMobsDealDamageTaskType(final BukkitQuestsPlugin plugin) {
        super("mythicmobs_dealdamage", TaskUtils.TASK_ATTRIBUTION_STRING, "Deal damage to specified MythicMobs mob.", MythicMobsDealDamageTask.SCHEMA);

        this.plugin = plugin;

        // MythicMobs 5
        if (CompatUtils.classExists("io.lumine.mythic.bukkit.MythicBukkit")) {
            entityConsumer = (entity, consumer) -> {
                //noinspection resource
                io.lumine.mythic.core.mobs.ActiveMob mob = MythicBukkit.inst()
                        .getMobManager()
                        .getActiveMob(entity.getUniqueId())
                        .orElse(null);

                if (mob == null) {
                    return;
                }

                String mobName = mob.getMobType();
                double level = mob.getLevel();

                consumer.accept(mobName, level);
            };

            return;
        }

        // MythicMobs 4
        if (CompatUtils.classExists("io.lumine.xikage.mythicmobs.MythicMobs")) {
            entityConsumer = (entity, consumer) -> {
                //noinspection resource
                io.lumine.xikage.mythicmobs.mobs.ActiveMob mob = MythicMobs.inst()
                        .getMobManager()
                        .getActiveMob(entity.getUniqueId())
                        .orElse(null);

                if (mob == null) {
                    return;
                }

                String mobName = mob.getMobType();
                double level = mob.getLevel();

                consumer.accept(mobName, level);
            };

            return;
        }

        plugin.getLogger().severe("Failed to register event handler for MythicMobs dealdamage task type!");
        plugin.getLogger().severe("MythicMobs version detected: " + CompatUtils.getPluginVersion("MythicMobs"));

        // By default, do nothing
        entityConsumer = (entity, consumer) -> {};
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        entityConsumer.accept(event.getEntity(), (mobName, level) -> this.handle(event, mobName, level));
    }

    private void handle(final EntityDamageEvent event, final String mobName, final double level) {
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

        // Clamp entity damage as getDamage() returns Float.MAX_VALUE for killing a parrot with a cookie
        // https://github.com/LMBishop/Quests/issues/753
        double finalDamage = event.getFinalDamage();
        double health = damageable.getHealth();
        double damage = Math.clamp(finalDamage, 0.0d, health);

        for (TaskUtils.PendingTask<MythicMobsDealDamageTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            MythicMobsDealDamageTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player damaged " + mobName + " for " + damage, quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, task.names, task.nameMatchMode, mobName, player.getUniqueId(), false, false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (level < task.minLevel) {
                super.debug("Minimum level is required and it is not high enough, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.level != -1 && level != task.level) {
                super.debug("Specific level is required and it does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.itemRequirement != null) {
                if (item == null) {
                    super.debug("Specific item is required, player has no item in hand; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                super.debug("Specific item is required; player held item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                if (!task.itemRequirement.matches(item)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
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
