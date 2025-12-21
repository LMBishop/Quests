package com.leonardobishop.quests.bukkit.tasktype.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
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

public final class MobkillingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final Table<String, String, QuestItem> fixedQuestItemCache = HashBasedTable.create();

    public MobkillingTaskType(BukkitQuestsPlugin plugin) {
        super("mobkilling", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a entity type.", "mobkillingcertain");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useEntityListConfigValidator(this, "mob", "mobs"));
        super.addConfigValidator(TaskUtils.useSpawnReasonListConfigValidator(this, "spawn-reason", "spawn-reasons"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "hostile"));
        super.addConfigValidator(TaskUtils.useItemStackConfigValidator(this, "item"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "data"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "exact-match"));
        super.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "name-match-mode"));

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

    @Override
    public void onReady() {
        fixedQuestItemCache.clear();
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

        //noinspection deprecation
        String customName = entity.getCustomName();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed " + entity.getType(), quest.getId(), task.getId(), player.getUniqueId());

            if (task.hasConfigKey("hostile")) {
                boolean hostile = TaskUtils.getConfigBoolean(task, "hostile");

                if (!hostile && !(entity instanceof Animals)) {
                    super.debug("Mob must be passive, but is hostile, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else if (hostile && !(entity instanceof Monster)) {
                    super.debug("Mob must be hostile, but is passive, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            if (!TaskUtils.matchEntity(this, pendingTask, entity, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchSpawnReason(this, pendingTask, entity, player.getUniqueId())) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchString(this, pendingTask, customName, player.getUniqueId(), "name", "names", true, "name-match-mode", false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (task.hasConfigKey("item")) {
                if (item == null) {
                    super.debug("Specific item is required, player has no item in hand; continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }

                super.debug("Specific item is required; player held item is of type '" + item.getType() + "'", quest.getId(), task.getId(), player.getUniqueId());

                QuestItem qi;
                if ((qi = fixedQuestItemCache.get(quest.getId(), task.getId())) == null) {
                    QuestItem fetchedItem = TaskUtils.getConfigQuestItem(task, "item", "data");
                    fixedQuestItemCache.put(quest.getId(), task.getId(), fetchedItem);
                    qi = fetchedItem;
                }

                boolean exactMatch = TaskUtils.getConfigBoolean(task, "exact-match", true);
                if (!qi.compareItemStack(item, exactMatch)) {
                    super.debug("Item does not match required item, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                } else {
                    super.debug("Item matches required item", quest.getId(), task.getId(), player.getUniqueId());
                }
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, eventAmount);
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
