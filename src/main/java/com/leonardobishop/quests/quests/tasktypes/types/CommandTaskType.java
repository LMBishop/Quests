package com.leonardobishop.quests.quests.tasktypes.types;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CommandTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public CommandTaskType() {
        super("command", "LMBishop", "Execute a certain command.");
        this.creatorConfigValues.add(new ConfigValue("command", true, "The command to execute."));
        this.creatorConfigValues.add(new ConfigValue("ignore-case", false, "Ignore the casing of the command."));
        this.creatorConfigValues.add(new ConfigValue("worlds", false, "Permitted worlds the player must be in."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".command", config.get("command"), problems, "command", super.getType());
        TaskUtils.configValidateBoolean(root + ".ignore-case", config.get("ignore-case"), problems, true, "ignore-case", super.getType());
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

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
