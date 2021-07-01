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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CommandTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CommandTaskType(BukkitQuestsPlugin plugin) {
        super("command", TaskUtils.TASK_ATTRIBUTION_STRING, "Execute a certain command.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".command", config.get("command"), problems, "command", super.getType());
        TaskUtils.configValidateBoolean(root + ".ignore-case", config.get("ignore-case"), problems, true, "ignore-case", super.getType());
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasMetadata("NPC")) return;

        Player player = e.getPlayer();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }
                    Object configCommand = task.getConfigValue("command");
                    Object configIgnoreCase = task.getConfigValue("ignore-case");

                    List<String> commands = new ArrayList<>();
                    if (configCommand instanceof List) {
                        commands.addAll((List) configCommand);
                    } else {
                        commands.add(String.valueOf(configCommand));
                    }

                    boolean ignoreCasing = false;
                    if (configIgnoreCase != null) {
                        ignoreCasing = (boolean) task.getConfigValue("ignore-case");
                    }
                    String message = e.getMessage();
                    if (message.length() >= 1) {
                        message = message.substring(1);
                    }

                    for (String command : commands) {
                        if (ignoreCasing && command.equalsIgnoreCase(message)) {
                            taskProgress.setCompleted(true);
                        } else if (!ignoreCasing && command.equals(message)) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }
}
