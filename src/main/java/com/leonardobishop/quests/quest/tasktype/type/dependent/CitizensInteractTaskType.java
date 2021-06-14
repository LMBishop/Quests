package com.leonardobishop.quests.quest.tasktype.type.dependent;

import com.leonardobishop.quests.util.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.quest.tasktype.ConfigValue;
import com.leonardobishop.quests.quest.tasktype.TaskType;
import com.leonardobishop.quests.quest.tasktype.TaskUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CitizensInteractTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public CitizensInteractTaskType() {
        super("citizens_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with an NPC to complete the quest.");
        this.creatorConfigValues.add(new ConfigValue("npc-name", true, "Name of the NPC."));
        this.creatorConfigValues.add(new ConfigValue("worlds", false, "Permitted worlds the player must be in."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        TaskUtils.configValidateExists(root + ".npc-name", config.get("npc-name"), problems, "npc-name", super.getType());
        TaskUtils.configValidateBoolean(root + ".remove-items-when-complete", config.get("remove-items-when-complete"), problems, true, "remove-items-when-complete", super.getType());
        return problems;
    }


    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCClick(NPCRightClickEvent event) {
        QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(event.getClicker().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(event.getClicker(), task)) continue;

                    if (!ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.valueOf(task.getConfigValue("npc-name")))).equals(ChatColor
                            .stripColor(ChatColor.translateAlternateColorCodes('&', event.getNPC().getName())))) {
                        return;
                    }
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    taskProgress.setCompleted(true);
                }
            }
        }
    }

}
