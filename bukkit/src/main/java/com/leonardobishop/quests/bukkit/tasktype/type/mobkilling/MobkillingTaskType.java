package com.leonardobishop.quests.bukkit.tasktype.type.mobkilling;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class MobkillingTaskType extends BukkitTaskType<MobkillingTask> {

    private final BukkitQuestsPlugin plugin;

    public MobkillingTaskType(BukkitQuestsPlugin plugin) {
        super("mobkilling", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a entity type.", MobkillingTask.SCHEMA, "mobkillingcertain");
        this.plugin = plugin;

        if (plugin.getQuestsConfig().getBoolean("options.mobkilling-use-wildstacker-hook", true)) {
            try {
                Class.forName("com.bgsoftware.wildstacker.api.events.EntityUnstackEvent");
                plugin.getServer().getPluginManager().registerEvents(new MobkillingTaskType.EntityUnstackListener(), plugin);
                return;
            } catch (ClassNotFoundException ignored) {
            } // there is no entity unstack available so we use EntityDeathEvent instead
        }

        plugin.getServer().getPluginManager().registerEvents(new MobkillingTaskType.EntityDeathListener(), plugin);
    }

    private final class EntityDeathListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onEntityDeath(EntityDeathEvent event) {
            LivingEntity entity = event.getEntity();
            Player killer = entity.getKiller();
            Player player;

            if (killer != null) {
                player = killer;
            } else {
                EntityDamageEvent damageEvent = entity.getLastDamageCause();
                player = plugin.getVersionSpecificHandler().getDamager(damageEvent);
            }

            handle(player, entity, 1);
        }
    }

    private final class EntityUnstackListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onEntityUnstack(com.bgsoftware.wildstacker.api.events.EntityUnstackEvent event) {
            Entity source = event.getUnstackSource();
            if (!(source instanceof Player player)) {
                return;
            }

            LivingEntity entity = event.getEntity().getLivingEntity();
            int eventAmount = event.getAmount();

            handle(player, entity, eventAmount);
        }
    }

    private void handle(Player player, LivingEntity entity, int eventAmount) {
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        if (entity instanceof Player) {
            return;
        }

        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
        Entity directSource = plugin.getVersionSpecificHandler().getDirectSource(lastDamageCause);
        ItemStack bowItem = directSource != null ? plugin.getProjectile2ItemCache().getItem(directSource) : null;
        ItemStack item = bowItem != null ? bowItem : plugin.getVersionSpecificHandler().getItemInMainHand(player);
        ItemStack projectileItem = directSource != null ? plugin.getVersionSpecificHandler().getAbstractArrowItem(directSource) : null;

        //noinspection deprecation
        String customName = entity.getCustomName();

        for (TaskUtils.PendingTask<MobkillingTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            MobkillingTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed " + entity.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (task.hostile != null) {
                if (!task.hostile && !(entity instanceof Animals)) {
                    super.debug("Mob must be passive, but is hostile, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else if (task.hostile && !(entity instanceof Monster)) {
                    super.debug("Mob must be hostile, but is passive, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            if (!TaskUtils.matchEntity(this, pendingTask, task.mobs, entity, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchSpawnReason(this, pendingTask, task.spawnReasons, entity, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchString(this, pendingTask, task.names, task.nameMatchMode, customName, player.getUniqueId(), true, false)) {
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

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, eventAmount);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
