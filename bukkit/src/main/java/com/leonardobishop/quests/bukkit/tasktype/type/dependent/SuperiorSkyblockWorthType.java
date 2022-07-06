package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.bgsoftware.superiorskyblock.api.events.IslandWorthUpdateEvent;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SuperiorSkyblockWorthType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public SuperiorSkyblockWorthType(BukkitQuestsPlugin plugin) {
        super("superiorskyblock_worth", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island worth for SuperiorSkyblock.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".worth", config.get("worth"), problems, "worth", super.getType()))
            TaskUtils.configValidateNumber(root + ".worth", config.get("worth"), problems, false, false, "worth");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLevel(IslandWorthUpdateEvent event) {
        for (SuperiorPlayer superiorPlayer : event.getIsland().getIslandMembers(true)) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(superiorPlayer.getUniqueId());
            if (qPlayer == null) {
                continue;
            }

            Player player = Bukkit.getPlayer(superiorPlayer.getUniqueId());

            if (player == null) {
                continue;
            }

            for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
                Quest quest = pendingTask.quest();
                Task task = pendingTask.task();
                TaskProgress taskProgress = pendingTask.taskProgress();

                super.debug("Player island worth updated", quest.getId(), task.getId(), player.getUniqueId());

                double islandLevelNeeded = Double.parseDouble(String.valueOf(task.getConfigValue("worth")));
                BigDecimal bd = new BigDecimal(islandLevelNeeded);

                taskProgress.setProgress(event.getNewLevel().doubleValue());
                super.debug("Updating task progress (now " + event.getNewLevel().doubleValue() + ")", quest.getId(), task.getId(), player.getUniqueId());

                if (event.getNewLevel().compareTo(bd) > 0) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }
}
