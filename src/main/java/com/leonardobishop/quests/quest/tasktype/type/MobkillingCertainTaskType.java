package com.leonardobishop.quests.quest.tasktype.type;

import com.leonardobishop.quests.util.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.quest.tasktype.ConfigValue;
import com.leonardobishop.quests.quest.tasktype.TaskType;
import com.leonardobishop.quests.quest.tasktype.TaskUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MobkillingCertainTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public MobkillingCertainTaskType() {
        super("mobkillingcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Kill a set amount of a specific entity type.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of mobs to be killed."));
        this.creatorConfigValues.add(new ConfigValue("mob", true, "Name of mob."));
        this.creatorConfigValues.add(new ConfigValue("name", false, "Only allow a specific name for mob (unspecified = any name allowed)."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".mob", config.get("mob"), problems, "mob", super.getType())) {
            try {
                EntityType.valueOf(String.valueOf(config.get("mob")));
            } catch (IllegalArgumentException ex) {
                problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                        QuestsConfigLoader.ConfigProblemDescriptions.UNKNOWN_ENTITY_TYPE.getDescription(String.valueOf(config.get("mob"))), root + ".mob"));
            }
        }
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }


    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
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

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(killer.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(killer, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    String configEntity = (String) task.getConfigValue("mob");

                    EntityType entity;
                    try {
                        entity = EntityType.valueOf(configEntity);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }

                    Object configName = task.getConfigValues().containsKey("name") ? task.getConfigValue("name") : task.getConfigValue("names");

                    if (configName != null) {
                        List<String> configNames = new ArrayList<>();
                        if (configName instanceof List) {
                            configNames.addAll((List) configName);
                        } else {
                            configNames.add(String.valueOf(configName));
                        }

                        boolean validName = false;
                        for (String name : configNames) {
                            name = ChatColor.translateAlternateColorCodes('&', name);
                            if (mob.getCustomName() == null || !mob.getCustomName().equals(name)) {
                                validName = true;
                                break;
                            }
                        }

                        if (!validName) continue;
                    }

                    if (mob.getType() != entity) {
                        continue;
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
