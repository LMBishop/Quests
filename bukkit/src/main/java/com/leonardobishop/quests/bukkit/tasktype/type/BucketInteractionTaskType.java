package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
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

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            int amount = (int) task.getConfigValue("amount");
            Object configBucket = task.getConfigValue("bucket");
            Material material = Material.getMaterial((String) configBucket);

            super.debug("Player used bucket of type " + bucket, quest.getId(), task.getId(), player.getUniqueId());

            if (bucket != material) {
                super.debug("Player bucket does not match required bucket '" + material + "', continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if ((int) taskProgress.getProgress() >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

}
