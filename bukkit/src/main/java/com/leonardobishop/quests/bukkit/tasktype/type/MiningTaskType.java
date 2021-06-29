package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MiningTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public MiningTaskType(BukkitQuestsPlugin plugin) {
        // type, author, description
        super("blockbreak", TaskUtils.TASK_ATTRIBUTION_STRING, "Break a set amount of blocks.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;  // citizens also causes these events to fire

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()); // get the qplayer so you can get their progress
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) { // iterate through all quests which are registered to use this task type
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) { // get all tasks of this type
                    if (!TaskUtils.validateWorld(event.getPlayer(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId()); // get the task progress and increment progress by 1

                    if (taskProgress.isCompleted()) { // dont need to increment a completed task
                        continue;
                    }

                    int brokenBlocksNeeded = (int) task.getConfigValue("amount"); // this will retrieve a value from the config under the key "value"

                    int progressBlocksBroken;
                    if (taskProgress.getProgress() == null) { // note: if the player has never progressed before, getProgress() will return null
                        progressBlocksBroken = 0;
                    } else {
                        progressBlocksBroken = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressBlocksBroken + 1); // the progress does not have to be an int, although must be serializable by the yaml provider

                    if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) { // completion statement, if true the task is complete
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
