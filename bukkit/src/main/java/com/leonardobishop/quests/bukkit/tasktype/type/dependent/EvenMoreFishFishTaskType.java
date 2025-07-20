package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.entity.Player;

public abstract class EvenMoreFishFishTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public EvenMoreFishFishTaskType(String type, String author, String description, BukkitQuestsPlugin plugin) {
        super(type, author, description);
        this.plugin = plugin;

        this.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        this.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        this.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "fish-match-mode"));
        this.addConfigValidator(TaskUtils.useEnumConfigValidator(this, TaskUtils.StringMatchMode.class, "rarity-match-mode"));
    }

    protected void handle(Player player, Fish fish) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        String fishName = fish.getName();
        String rarityName = fish.getRarity().getId();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            this.debug("Player caught fish " + fishName + " of rarity " + rarityName, quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, fishName, player.getUniqueId(), "fish", "fishes", false, "fish-match-mode", false)) {
                this.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchString(this, pendingTask, rarityName, player.getUniqueId(), "rarity", "rarities", false, "rarity-match-mode", false)) {
                this.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            this.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                this.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
