package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
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
        super("mobkillingcertain", "LMBishop", "Kill a set amount of a specific entity type.");
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

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(killer.getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(killer, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    String configEntity = (String) task.getConfigValue("mob");
                    String configName = (String) task.getConfigValue("name");

                    EntityType entity;
                    try {
                        entity = EntityType.valueOf(configEntity);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }

                    if (configName != null) {
                        configName = ChatColor.translateAlternateColorCodes('&', configName);
                        if (mob.getCustomName() == null || !mob.getCustomName().equals(configName)) {
                            continue;
                        }
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
