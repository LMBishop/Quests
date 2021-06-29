package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PlaceholderAPIEvaluateTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private BukkitTask poll;

    public PlaceholderAPIEvaluateTaskType(BukkitQuestsPlugin plugin) {
        super("placeholderapi_evaluate", TaskUtils.TASK_ATTRIBUTION_STRING, "Evaluate the result of a placeholder");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".placeholder", config.get("placeholder"), problems, "placeholder", super.getType());
        boolean evalExists = TaskUtils.configValidateExists(root + ".evaluates", config.get("evaluates"), problems, "evaluates", super.getType());

        if (config.containsKey("operator")) {
            String operatorStr = (String) config.get("operator");
            Operator operator = null;
            try {
                operator = Operator.valueOf(operatorStr);
            } catch (IllegalArgumentException ex) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        "Operator '" + operatorStr + "' does not exist.", root + ".operator"));
            }
            if (operator != null && evalExists) {
                String evalStr = String.valueOf(config.get("evaluates"));
                try {
                    Double.parseDouble(evalStr);
                } catch (IllegalArgumentException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            "Numeric operator specified, but placeholder evaluation '" + evalStr + "' is not numeric.", root + ".evaluates"));
                }
            }
        }
        return problems;
    }

    @Override
    public void onReady() {
        this.poll = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        continue;
                    }

                    for (Quest quest : PlaceholderAPIEvaluateTaskType.super.getRegisteredQuests()) {
                        if (qPlayer.hasStartedQuest(quest)) {
                            QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
                            for (Task task : quest.getTasksOfType(PlaceholderAPIEvaluateTaskType.super.getType())) {
                                if (!TaskUtils.validateWorld(player, task)) continue;
                                TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
                                if (taskProgress.isCompleted()) {
                                    continue;
                                }
                                String placeholder = (String) task.getConfigValue("placeholder");
                                String evaluates = String.valueOf(task.getConfigValue("evaluates"));
                                String configOperator = (String) task.getConfigValue("operator");
                                Operator operator = null;
                                if (configOperator != null) {
                                    try {
                                        operator = Operator.valueOf(configOperator);
                                    } catch (IllegalArgumentException ignored) { }
                                }
                                if (placeholder != null && evaluates != null) {
                                    double numericEvaluates = 0;
                                    if (operator != null) {
                                        try {
                                            numericEvaluates = Double.parseDouble(evaluates);
                                        } catch (NumberFormatException ex) {
                                            continue;
                                        }
                                    }

                                    String evaluated = PlaceholderAPI.setPlaceholders(player, placeholder);
                                    if (operator == null && evaluated.equals(evaluates)) {
                                        taskProgress.setCompleted(true);
                                    } else if (operator != null) {
                                        double numericEvaluated;
                                        try {
                                            numericEvaluated = Double.parseDouble(evaluated);
                                        } catch (NumberFormatException ex) {
                                            continue;
                                        }
                                        switch (operator) {
                                            case GREATER_THAN:
                                                if (numericEvaluated > numericEvaluates)
                                                    taskProgress.setCompleted(true);
                                                continue;
                                            case LESS_THAN:
                                                if (numericEvaluated < numericEvaluates)
                                                    taskProgress.setCompleted(true);
                                                continue;
                                            case GREATER_THAN_OR_EQUAL_TO:
                                                if (numericEvaluated >= numericEvaluates)
                                                    taskProgress.setCompleted(true);
                                                continue;
                                            case LESS_THAN_OR_EQUAL_TO:
                                                if (numericEvaluated <= numericEvaluates)
                                                    taskProgress.setCompleted(true);
                                                continue;
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 30L, 30L);
    }

    @Override
    public void onDisable() {
        if (this.poll != null) {
            this.poll.cancel();
        }
    }

    enum Operator {
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL_TO,
        LESS_THAN_OR_EQUAL_TO;
    }
}
