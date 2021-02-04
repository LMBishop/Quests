package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.leonardobishop.quests.Quests;
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
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class PlaceholderAPIEvaluateTaskType extends TaskType {

    private BukkitTask poll;
    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public PlaceholderAPIEvaluateTaskType() {
        super("placeholderapi_evaluate", "LMBishop", "Evaluate the result of a placeholder");
        this.creatorConfigValues.add(new ConfigValue("placeholder", true, "The placeholder string (including %%)."));
        this.creatorConfigValues.add(new ConfigValue("evaluates", true, "What it should evaluate to be marked as complete."));
    }

    @Override
    public void onReady() {
        this.poll = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
                    QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
                    for (Quest quest : PlaceholderAPIEvaluateTaskType.super.getRegisteredQuests()) {
                        if (questProgressFile.hasStartedQuest(quest)) {
                            QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
                            for (Task task : quest.getTasksOfType(PlaceholderAPIEvaluateTaskType.super.getType())) {
                                if (!TaskUtils.validateWorld(player, task)) continue;
                                TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
                                if (taskProgress.isCompleted()) {
                                    continue;
                                }
                                String placeholder = (String) task.getConfigValue("placeholder");
                                String evaluates = (String) task.getConfigValue("evaluates");
                                if (placeholder != null) {
                                    if (PlaceholderAPI.setPlaceholders(player, placeholder).equals(evaluates)) {
                                        taskProgress.setCompleted(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Quests.get(), 30L, 30L);
    }

    @Override
    public void onDisable() {
        if (this.poll != null) {
            this.poll.cancel();
        }
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }
}
