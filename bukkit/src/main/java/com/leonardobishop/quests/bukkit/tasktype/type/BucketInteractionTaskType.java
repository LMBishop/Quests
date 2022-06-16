package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BucketInteractionTaskType extends BukkitTaskType {

    public BucketInteractionTaskType(@NotNull String type, String author, String description) {
        super(type, author, description);
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (config.get("bucket") == null) {
            TaskUtils.configValidateExists(root + ".bucket", config.get("bucket"), problems, "bucket", super.getType());
        } else {
            String configBlock = config.get("bucket").toString();

            String[] split = configBlock.split(":");
            if (Material.getMaterial(String.valueOf(split[0])) == null) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(configBlock),
                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getExtendedDescription(configBlock),
                        root + ".bucket"));
            }
        }
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    public void onBucket(Player player, Material bucket, BukkitQuestsPlugin plugin) {
        if (!player.isOnline() || player.hasMetadata("NPC") || bucket == null) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (qPlayer == null) return;

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    int amount = (int) task.getConfigValue("amount");
                    Object configBucket = task.getConfigValue("bucket");
                    Material material = Material.getMaterial((String) configBucket);

                    if (bucket != material) {
                        continue;
                    }

                    int progress;
                    if (taskProgress.getProgress() == null) {
                        progress = 0;
                    } else {
                        progress = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progress + 1);

                    if ((int) taskProgress.getProgress() >= amount) {
                        taskProgress.setProgress(amount);
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
