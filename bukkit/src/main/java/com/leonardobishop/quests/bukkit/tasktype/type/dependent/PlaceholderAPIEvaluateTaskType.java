package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

public final class PlaceholderAPIEvaluateTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final WeakHashMap<Player, WeakHashMap<Task, Integer>> refreshTicksMap = new WeakHashMap<>();
    private WrappedTask poll;

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
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "refresh-ticks"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "async"));
    }

    @Override
    public void onReady() {
        int refreshTicks = plugin.getConfig().getInt("options.placeholderapi-global-refresh-ticks", 30);
        this.poll = new WrappedRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handle(player);
                }
            }
        }.runTaskTimer(plugin.getScheduler(), refreshTicks, refreshTicks);
    }

    private void handle(Player player) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Polling PAPI for player", quest.getId(), task.getId(), player.getUniqueId());

            Integer refreshTicks = (Integer) task.getConfigValue("refresh-ticks");
            if (refreshTicks != null) {
                int currentTick = Bukkit.getCurrentTick();

                WeakHashMap<Task, Integer> playerRefreshTicksMap = refreshTicksMap.computeIfAbsent(player, k -> new WeakHashMap<>());
                Integer lastRefreshTicks = playerRefreshTicksMap.get(task);

                if (lastRefreshTicks != null) {
                    int ticksSinceLastRefresh = currentTick - lastRefreshTicks;

                    if (ticksSinceLastRefresh < refreshTicks) {
                        super.debug("Ticks since last refresh are lower than specified, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }
                }

                playerRefreshTicksMap.put(task, currentTick);
            }

            String placeholder = (String) task.getConfigValue("placeholder");
            if (placeholder == null) {
                continue;
            }

            String evaluatesString = String.valueOf(task.getConfigValue("evaluates"));
            if (evaluatesString == null) {
                continue;
            }

            String operatorString = (String) task.getConfigValue("operator");
            Operator operator;
            if (operatorString != null) {
                try {
                    operator = Operator.valueOf(operatorString);
                } catch (IllegalArgumentException ignored) {
                    super.debug("Numeric operator was specified but cannot be parsed, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
                operator = null;
            }

            super.debug("Operator = " + operator, quest.getId(), task.getId(), player.getUniqueId());

            boolean async = TaskUtils.getConfigBoolean(task, "async", false);
            CompletableFuture<String> future = evaluate(player, placeholder, async);

            future.thenAccept(evaluatedString -> {
                super.debug("Evaluation = '" + evaluatedString + "'", quest.getId(), task.getId(), player.getUniqueId());

                if (operator != null) {
                    double evaluates;
                    try {
                        evaluates = Double.parseDouble(evaluatesString);
                    } catch (NumberFormatException ignored) {
                        super.debug("Numeric operator was specified but configured string to evaluate to cannot be parsed into a double, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        return;
                    }

                    double evaluated;
                    try {
                        evaluated = Double.parseDouble(evaluatedString);
                    } catch (NumberFormatException ignored) {
                        super.debug("Numeric operator was specified but evaluated string to cannot be parsed into a double, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        return;
                    }

                    taskProgress.setProgress(evaluated);

                    if (operator.compare(evaluated, evaluates)) {
                        super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                        taskProgress.setCompleted(true);
                    }

                    // Do not send track advancement for this task type as it behaves really weird
                    //TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, evaluates);
                } else if (evaluatedString.equals(evaluatesString)) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            });
        }
    }

    private CompletableFuture<String> evaluate(Player player, String placeholder, boolean async) {
        if (!async) {
            String evaluated = PlaceholderAPI.setPlaceholders(player, placeholder);
            return CompletableFuture.completedFuture(evaluated);
        }

        CompletableFuture<String> future = new CompletableFuture<>();

        plugin.getScheduler().runTaskAsynchronously(() -> {
            String evaluated = PlaceholderAPI.setPlaceholders(player, placeholder);
            plugin.getScheduler().runTaskAtEntity(player, () -> future.complete(evaluated));
        });

        return future;
    }

    @Override
    public void onDisable() {
        if (this.poll != null) {
            this.poll.cancel();
        }
    }

    private enum Operator {
        GREATER_THAN {
            @Override
            public boolean compare(double evaluated, double evaluates) {
                return evaluated > evaluates;
            }
        },
        LESS_THAN {
            @Override
            public boolean compare(double evaluated, double evaluates) {
                return evaluated < evaluates;
            }
        },
        GREATER_THAN_OR_EQUAL_TO {
            @Override
            public boolean compare(double evaluated, double evaluates) {
                return evaluated >= evaluates;
            }
        },
        LESS_THAN_OR_EQUAL_TO {
            @Override
            public boolean compare(double evaluated, double evaluates) {
                return evaluated <= evaluates;
            }
        };

        public abstract boolean compare(double evaluated, double evaluates);
    }

    @Override
    public @NonNull Object getGoal(final @NonNull Task task) {
        return task.getConfigValue("evaluates", "-");
    }
}
