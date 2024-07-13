package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class InteractTaskType<T> extends BukkitTaskType {

    public InteractTaskType(String type, String author, String description) {
        super(type, author, description);

        super.addConfigValidator((config, problems) -> {
            if (config.containsKey("npc-name") && config.containsKey("npc-id")) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        "Both npc-name and npc-id is specified; npc-name will be ignored", null, "npc-name"));
            }
        });
    }

    public abstract List<T> getNPCId(Task task);

    public void handle(Player player, T npcId, String npcName, BukkitQuestsPlugin plugin) {
        if (!player.isOnline()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        boolean nameCorrected = false;

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player clicked NPC", quest.getId(), task.getId(), player.getUniqueId());

            List<T> configNPCId = getNPCId(task);
            if (configNPCId != null) {
                if (!configNPCId.contains(npcId)) {
                    super.debug("NPC id " + npcId + " does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else {
                String configNPCName = (String) task.getConfigValue("npc-name");
                if (configNPCName != null) {
                    if (npcName == null) {
                        super.debug("NPC name is empty and does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }

                    if (!nameCorrected) {
                        npcName = Chat.legacyStrip(Chat.legacyColor(npcName));
                        nameCorrected = true;
                    }

                    if (!configNPCName.equals(npcName)) {
                        super.debug("NPC name " + npcName + " does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                        continue;
                    }
                }
            }

            super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
            taskProgress.setCompleted(true);
        }
    }
}
