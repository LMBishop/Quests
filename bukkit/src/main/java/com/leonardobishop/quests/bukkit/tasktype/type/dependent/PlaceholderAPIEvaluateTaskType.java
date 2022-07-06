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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class PlaceholderAPIEvaluateTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private BukkitTask poll;

    public PlaceholderAPIEvaluateTaskType(BukkitQuestsPlugin plugin) {
        super("placeholderapi_evaluate", TaskUtils.TASK_ATTRIBUTION_STRING, "Evaluate the result of a placeholder");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "placeholder"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "evaluates"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Arrays.asList(
                "GREATER_THAN",
                "GREATER_THAN_OR_EQUAL_TO",
                "LESS_THAN",
                "LESS_THAN_OR_EQUAL_TO"
        ),"operator"));
        super.addConfigValidator((config, problems) -> {
            if (config.containsKey("operator")) {
                String evalStr = String.valueOf(config.get("evaluates"));
                try {
                    Double.parseDouble(evalStr);
                } catch (IllegalArgumentException ex) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            "Numeric operator specified, but placeholder evaluation '" + evalStr + "' is not numeric",
                            "A numeric operator was specified, but<br>" +
                            "the evaluation '" + evalStr + "' is not numeric<br>" +
                            "and cannot be parsed.",
                            "evaluates"));
                }
            }
        });
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
//        TaskUtils.configValidateExists(root + ".placeholder", config.get("placeholder"), problems, "placeholder", super.getType());
//        boolean evalExists = TaskUtils.configValidateExists(root + ".evaluates", config.get("evaluates"), problems, "evaluates", super.getType());
//
//        if (config.containsKey("operator")) {
//            String operatorStr = (String) config.get("operator");
//            Operator operator = null;
//            try {
//                operator = Operator.valueOf(operatorStr);
//            } catch (IllegalArgumentException ex) {
//                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
//                        "Operator '" + operatorStr + "' does not exist",
//                        "Valid operators are:<br>" +
//                                "- GREATER_THAN<br>" +
//                                "- LESS_THAN<br>" +
//                                "- GREATER_THAN_OR_EQUAL_TO<br>" +
//                                "- LESS_THAN_OR_EQUAL_TO<br>",
//                        root + ".operator"));
//            }
//            if (operator != null && evalExists) {
//                String evalStr = String.valueOf(config.get("evaluates"));
//                try {
//                    Double.parseDouble(evalStr);
//                } catch (IllegalArgumentException ex) {
//                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
//                            "Numeric operator specified, but placeholder evaluation '" + evalStr + "' is not numeric", null, root + ".evaluates"));
//                }
//            }
//        }
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

                    for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, PlaceholderAPIEvaluateTaskType.this)) {
                        Quest quest = pendingTask.quest();
                        Task task = pendingTask.task();
                        TaskProgress taskProgress = pendingTask.taskProgress();

                        PlaceholderAPIEvaluateTaskType.super.debug("Polling PAPI for player", quest.getId(), task.getId(), player.getUniqueId());

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
                                    PlaceholderAPIEvaluateTaskType.super.debug("Numeric operator was specified but configured string to evaluate to cannot be parsed into a double, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                                    continue;
                                }
                            }


                            String evaluated = PlaceholderAPI.setPlaceholders(player, placeholder);
                            PlaceholderAPIEvaluateTaskType.super.debug("Evaluation = '" + evaluated + "'", quest.getId(), task.getId(), player.getUniqueId());
                            if (operator == null && evaluated.equals(evaluates)) {
                                PlaceholderAPIEvaluateTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                taskProgress.setCompleted(true);
                            } else if (operator != null) {
                                double numericEvaluated;
                                try {
                                    numericEvaluated = Double.parseDouble(evaluated);
                                } catch (NumberFormatException ex) {
                                    PlaceholderAPIEvaluateTaskType.super.debug("Numeric operator was specified but evaluated string cannot be parsed into a double, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                                    continue;
                                }
                                PlaceholderAPIEvaluateTaskType.super.debug("Operator = " + operator, quest.getId(), task.getId(), player.getUniqueId());
                                taskProgress.setProgress(numericEvaluated);
                                switch (operator) {
                                    case GREATER_THAN -> {
                                        if (numericEvaluated > numericEvaluates) {
                                            PlaceholderAPIEvaluateTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                            taskProgress.setCompleted(true);
                                        }
                                    }
                                    case LESS_THAN -> {
                                        if (numericEvaluated < numericEvaluates) {
                                            PlaceholderAPIEvaluateTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                            taskProgress.setCompleted(true);
                                        }
                                    }
                                    case GREATER_THAN_OR_EQUAL_TO -> {
                                        if (numericEvaluated >= numericEvaluates) {
                                            PlaceholderAPIEvaluateTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                            taskProgress.setCompleted(true);
                                        }
                                    }
                                    case LESS_THAN_OR_EQUAL_TO -> {
                                        if (numericEvaluated <= numericEvaluates) {
                                            PlaceholderAPIEvaluateTaskType.super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                                            taskProgress.setCompleted(true);
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
