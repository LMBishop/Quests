package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MythicMobsKillingType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MythicMobsKillingType(BukkitQuestsPlugin plugin, String mythicMobsVersion) {
        super("mythicmobs_killing", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a MythicMobs entity.");
        this.plugin = plugin;

        // MythicMobs 4
        try {
            Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent");
            plugin.getServer().getPluginManager().registerEvents(new MythicMobs4Listener(), plugin);
            return;
        } catch (ClassNotFoundException | NoSuchFieldException ignored) { } // MythicMobs version cannot support task type

        // MythicMobs 5
        try {
            Class.forName("io.lumine.mythic.bukkit.events.MythicMobDeathEvent");
            plugin.getServer().getPluginManager().registerEvents(new MythicMobs5Listener(), plugin);
            return;
        } catch (ClassNotFoundException ignored) { } // MythicMobs version cannot support task type

        plugin.getLogger().severe("Failed to register event handler for MythicMobs task type!");
        plugin.getLogger().severe("MythicMobs version detected: " + mythicMobsVersion);
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".name", config.get("name"), problems, "name", super.getType());
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, true, true, "level");
        TaskUtils.configValidateInt(root + ".min-level", config.get("min-level"), problems, true, true, "min-level");
        return problems;
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
        if (killer == null) {
            return;
        }

        if (mob == null || mob instanceof Player) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(killer.getWorld().getName(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    String configName = (String) task.getConfigValue("name");
                    int minMobLevel = (int) task.getConfigValue("min-level", -1);
                    int requiredLevel = (int) task.getConfigValue("level", -1);

                    if (!mobName.equals(configName) || level < minMobLevel) {
                        return;
                    }

                    if (requiredLevel != -1 && level != requiredLevel) {
                        return;
                    }

                    int mobKillsNeeded = (int) task.getConfigValue("amount");

                    int progressKills;
                    if (taskProgress.getProgress() == null) {
                        progressKills = 0;
                    } else {
                        progressKills = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressKills + 1);

                    if (((int) taskProgress.getProgress()) >= mobKillsNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
