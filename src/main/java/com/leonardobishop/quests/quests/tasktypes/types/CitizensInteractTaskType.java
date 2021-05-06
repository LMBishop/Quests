package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public final class CitizensInteractTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public CitizensInteractTaskType() {
        super("citizens_interact", "LMBishop", "Interact with an NPC to complete the quest.");
        this.creatorConfigValues.add(new ConfigValue("npc-name", true, "Name of the NPC."));
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

        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
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
