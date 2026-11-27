package com.leonardobishop.quests.bukkit.tasktype.type.dependent.evenmorefish;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.oheers.fish.api.fishing.items.IFish;
import org.bukkit.entity.Player;

public abstract class EvenMoreFishFishTaskType extends BukkitTaskType<EvenMoreFishFishTask> {

    private final BukkitQuestsPlugin plugin;

    public EvenMoreFishFishTaskType(String type, String author, String description, BukkitQuestsPlugin plugin) {
        super(type, author, description, EvenMoreFishFishTask.SCHEMA);
        this.plugin = plugin;
    }

    protected void handle(Player player, IFish fish) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        String fishName = fish.getName();
        String rarityName = fish.getRarity().getId();

        for (TaskUtils.PendingTask<EvenMoreFishFishTask> pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            EvenMoreFishFishTask task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            this.debug("Player caught fish " + fishName + " of rarity " + rarityName, quest.getId(), task.getId(), player.getUniqueId());

            if (!TaskUtils.matchString(this, pendingTask, task.fishes, task.fishMatchMode, fishName, player.getUniqueId(), false, false)) {
                this.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            if (!TaskUtils.matchString(this, pendingTask, task.rarities, task.rarityMatchMode, rarityName, player.getUniqueId(), false, false)) {
                this.debug("Continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            this.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= task.amount) {
                this.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, task.amount);
        }
    }
}
