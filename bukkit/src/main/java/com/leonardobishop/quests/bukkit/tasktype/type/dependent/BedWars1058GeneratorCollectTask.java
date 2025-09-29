package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.andrei1058.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public final class BedWars1058GeneratorCollectTask extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public BedWars1058GeneratorCollectTask(BukkitQuestsPlugin plugin) {
        super("bedwars1058_generator_collect", TaskUtils.TASK_ATTRIBUTION_STRING, "Collect specific items from BedWars1058 generators.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "item"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGeneratorCollect(PlayerGeneratorCollectEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        ItemStack collectedStack = event.getItemStack();
        if (collectedStack == null) {
            return;
        }

        String collectedMaterial = collectedStack.getType().name();
        int collectedAmount = collectedStack.getAmount();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            String requiredId = (String) task.getConfigValue("item");
            if (requiredId == null) {
                continue;
            }

            if (!requiredId.equalsIgnoreCase(collectedMaterial)) {
                super.debug("Collected " + collectedMaterial + " does not match required " + requiredId,
                        quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            super.debug("Player collected required item (" + collectedMaterial + ")",
                    quest.getId(), task.getId(), player.getUniqueId());

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, collectedAmount);
            int amount = ((int) task.getConfigValue("amount"));

            super.debug("Progress " + progress + "/" + amount,
                    quest.getId(), task.getId(), player.getUniqueId());

            if (progress >= amount) {
                taskProgress.setCompleted(true);
                super.debug("Marking task as complete",
                        quest.getId(), task.getId(), player.getUniqueId());
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
