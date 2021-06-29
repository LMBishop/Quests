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
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ExpEarnTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public ExpEarnTaskType(BukkitQuestsPlugin plugin) {
        super("expearn", TaskUtils.TASK_ATTRIBUTION_STRING, "Earn a set amount of exp.");
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
    public void onExpEarn(PlayerExpChangeEvent e) {
        if (e.getPlayer().hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }


        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
                
                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(e.getPlayer(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
                    
                    if (taskProgress.isCompleted()) {
                        continue;
                    }
                    int amount = e.getAmount();
                    int expNeeded = (int) task.getConfigValue("amount");
                    
                    int progressExp;
                    if (taskProgress.getProgress() == null) {
                        progressExp = 0;
                    } else {
                        progressExp = (int) taskProgress.getProgress();
                    }
                    
                    taskProgress.setProgress(progressExp + amount);
                    
                    if (((int) taskProgress.getProgress()) >= expNeeded) {
                        taskProgress.setCompleted(true);
                    }                    
                }
            }
        }        
    }
}
