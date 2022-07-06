package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CitizensInteractTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public CitizensInteractTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with an NPC to complete the quest.");
        this.plugin = plugin;

        super.addConfigValidator((config, problems) -> {
            if (config.containsKey("npc-name") && config.containsKey("npc-id")) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        "Both npc-name and npc-id is specified; npc-name will be ignored", null, "npc-name"));
            }
        });
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (!config.containsKey("npc-name")) {
            TaskUtils.configValidateExists(root + ".npc-id", config.get("npc-id"), problems, "npc-id", super.getType());
        } else {
            TaskUtils.configValidateExists(root + ".npc-name", config.get("npc-name"), problems, "npc-name", super.getType());
        }
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCClick(NPCRightClickEvent event) {
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getClicker().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Player player = event.getClicker();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player clicked NPC", quest.getId(), task.getId(), player.getUniqueId());

            if (task.getConfigValue("npc-id") != null) {
                if (!task.getConfigValue("npc-id").equals(event.getNPC().getId())) {
                    super.debug("NPC id ('" + event.getNPC().getId() + "') does not match required id, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            } else if (task.getConfigValue("npc-name") != null) {
                String npcName = Chat.legacyStrip(Chat.legacyColor(event.getNPC().getName()));
                super.debug("NPC name is required, current name = '" + npcName + "'", quest.getId(), task.getId(), player.getUniqueId());
                if (!Chat.legacyStrip(Chat.legacyColor(String.valueOf(task.getConfigValue("npc-name"))))
                        .equals(npcName)) {
                    super.debug("NPC name does not match required name, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                    continue;
                }
            }

            super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
            taskProgress.setCompleted(true);
        }
    }

}
