package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MobkillingCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MobkillingCertainTaskType(BukkitQuestsPlugin plugin) {
        super("mobkillingcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a specific entity type.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (config.get("mob") == null && config.get("mobs") == null) {
            TaskUtils.configValidateExists(root + ".mob", config.get("mob"), problems, "mob", super.getType());
        } else {
            Object configMob;
            String source;
            if (config.containsKey("mob")) {
                source = "mob";
            } else {
                source = "mobs";
            }
            configMob = config.get(source);
            List<String> checkBlocks = new ArrayList<>();
            if (configMob instanceof List) {
                checkBlocks.addAll((List) configMob);
            } else {
                checkBlocks.add(String.valueOf(configMob));
            }
            for (String mobName : checkBlocks) {
                try {
                    EntityType.valueOf(mobName);
                } catch (IllegalArgumentException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE.getDescription(mobName),
                            ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE.getExtendedDescription(mobName),
                            root + "." + source));
                }
            }

        }
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (mob == null || mob instanceof Player) {
            return;
        }

        if (killer == null) {
            return;
        }

        if (killer.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(killer, qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            List<String> configEntities = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("mob") ? "mob" : "mobs");

            super.debug("Player killed " + mob.getType(), quest.getId(), task.getId(), killer.getUniqueId());

            boolean validMob = false;
            for (String entry : configEntities) {
                super.debug("Checking against mob '" + entry + "'", quest.getId(), task.getId(), killer.getUniqueId());
                try {
                    EntityType entity = EntityType.valueOf(entry);
                    if (mob.getType() == entity) {
                        super.debug("Mob is valid", quest.getId(), task.getId(), killer.getUniqueId());
                        validMob = true;
                        break;
                    }
                } catch (IllegalArgumentException ignored) { }
            }

            if (!validMob) {
                super.debug("Mob is not in list of required mobs, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                continue;
            }

            List<String> configNames = TaskUtils.getConfigStringList(task, task.getConfigValues().containsKey("name") ? "name" : "names");

            if (!configNames.isEmpty()) {
                super.debug("List of required names exists; mob name is '" + Chat.legacyStrip(mob.getCustomName()) + "'", quest.getId(), task.getId(), killer.getUniqueId());

                boolean validName = false;
                for (String name : configNames) {
                    super.debug("Checking against name '" + name + "'", quest.getId(), task.getId(), killer.getUniqueId());
                    name = Chat.legacyColor(name);
                    if (mob.getCustomName() != null && !mob.getCustomName().equals(name)) {
                        super.debug("Mob has valid name", quest.getId(), task.getId(), killer.getUniqueId());
                        validName = true;
                        break;
                    }
                }

                if (!validName) {
                    super.debug("Mob name is not in list of valid name, continuing...", quest.getId(), task.getId(), killer.getUniqueId());
                    continue;
                }
            }

            int mobKillsNeeded = (int) task.getConfigValue("amount");

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), killer.getUniqueId());

            if (progress >= mobKillsNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), killer.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}
