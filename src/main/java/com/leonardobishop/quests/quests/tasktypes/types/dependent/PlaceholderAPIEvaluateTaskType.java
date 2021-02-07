package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
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
import net.objecthunter.exp4j.operator.Operator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PlaceholderAPIEvaluateTaskType extends TaskType {

    private BukkitTask poll;
    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public PlaceholderAPIEvaluateTaskType() {
        super("placeholderapi_evaluate", "LMBishop", "Evaluate the result of a placeholder");
        this.creatorConfigValues.add(new ConfigValue("placeholder", true, "The placeholder string (including %%)."));
        this.creatorConfigValues.add(new ConfigValue("evaluates", true, "What it should evaluate to be marked as complete."));
        this.creatorConfigValues.add(new ConfigValue("operator", false, "Comparison method."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".placeholder", config.get("placeholder"), problems, "placeholder", super.getType());
        boolean evalExists = TaskUtils.configValidateExists(root + ".evaluates", config.get("evaluates"), problems, "evaluates", super.getType());

        if (config.containsKey("operator")) {
            String operatorStr = (String) config.get("operator");
            Operator operator = null;
            try {
                operator = Operator.valueOf(operatorStr);
            } catch (IllegalArgumentException ex) {
                problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
                        "Operator '" + operatorStr + "' does not exist.", root + ".operator"));
            }
            if (operator != null && evalExists) {
                String evalStr = String.valueOf(config.get("evaluates"));
                try {
                    Double.parseDouble(evalStr);
                } catch (IllegalArgumentException ex) {
                    problems.add(new QuestsConfigLoader.ConfigProblem(QuestsConfigLoader.ConfigProblemType.WARNING,
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

    enum Operator {
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL_TO,
        LESS_THAN_OR_EQUAL_TO;
    }
}
