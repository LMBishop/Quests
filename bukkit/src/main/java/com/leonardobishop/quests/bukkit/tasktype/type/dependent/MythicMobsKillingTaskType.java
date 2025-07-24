package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;

public final class MythicMobsKillingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MythicMobsKillingTaskType(BukkitQuestsPlugin plugin) {
        super("mythicmobs_killing", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a MythicMobs entity.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "name", "names"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "level"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "min-level"));
        super.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "name-match-mode"));

        // MythicMobs 5
        try {
            Class.forName("io.lumine.mythic.bukkit.events.MythicMobDeathEvent");
            plugin.getServer().getPluginManager().registerEvents(new MythicMobs5Listener(), plugin);
            return;
        } catch (ClassNotFoundException ignored) { } // MythicMobs version cannot support task type

        // MythicMobs 4
        try {
            Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent");
            plugin.getServer().getPluginManager().registerEvents(new MythicMobs4Listener(), plugin);
            return;
        } catch (ClassNotFoundException | NoSuchFieldException ignored) { } // MythicMobs version cannot support task type

        plugin.getLogger().severe("Failed to register event handler for MythicMobs killing task type!");
        plugin.getLogger().severe("MythicMobs version detected: " + CompatUtils.getPluginVersion("MythicMobs"));
    }

    private final class MythicMobs4Listener implements Listener {

        private final Field levelField;

        public MythicMobs4Listener() throws ClassNotFoundException, NoSuchFieldException {
            // Fixes https://github.com/LMBishop/Quests/issues/318
            levelField = Class.forName("io.lumine.xikage.mythicmobs.mobs.ActiveMob").getDeclaredField("level");
            levelField.setAccessible(true);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onMythicMobs4MobDeath(io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent event) {
            // Fixes https://github.com/LMBishop/Quests/issues/318
            double level;
            try {
                level = ((Number) levelField.get(event.getMob())).doubleValue();
            } catch (Exception ignored) {
                // It should never happen
                return;
            }

            handle(event.getKiller(), event.getEntity(), event.getMobType().getInternalName(), level);
        }
    }

    private final class MythicMobs5Listener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onMythicMobs5MobDeath(io.lumine.mythic.bukkit.events.MythicMobDeathEvent event) {
            handle(event.getKiller(), event.getEntity(), event.getMobType().getInternalName(), event.getMobLevel());
        }
    }

    private void handle(LivingEntity killer, Entity mob, String mobName, double level) {
        if (!(killer instanceof Player player)) {
            return;
        }

        if (mob == null || mob instanceof Player) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player killed mythic mob '" + mobName + "' (level = " + level + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, mobName, player.getUniqueId(), "name", "names", false, "name-match-mode", false)) {
                super.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int minMobLevel = (int) task.getConfigValue("min-level", -1);
            if (level < minMobLevel) {
                super.debug("Minimum level is required and it is not high enough, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int requiredLevel = (int) task.getConfigValue("level", -1);
            if (requiredLevel != -1 && level != requiredLevel) {
                super.debug("Specific level is required and it does not match, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

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
