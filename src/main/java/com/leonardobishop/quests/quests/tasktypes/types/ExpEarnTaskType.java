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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.ArrayList;
import java.util.List;

public final class ExpEarnTaskType extends TaskType {
	
	private List<ConfigValue> creatorConfigValues = new ArrayList<>();
	
	public ExpEarnTaskType() {
		super("expearn", "toasted", "Earn a set amount of exp.");
		this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of exp that needs to be earned."));
	}
	
	@Override
	public List<ConfigValue> getCreatorConfigValues() {
		return creatorConfigValues;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExpEarn(PlayerExpChangeEvent e) {
		QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
		
		for (Quest quest : super.getRegisteredQuests()) {
			if (questProgressFile.hasStartedQuest(quest)) {
				QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
				
				for (Task task : quest.getTasksOfType(super.getType())) {
					TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
					
					if (taskProgress.isCompleted()) {
						continue;
					}
					int amount = e.getAmount();
					int expNeeded = (int) task.getConfigValue("amount");
					
					int progressExp;
					if (taskProgress.getProgress() == null) {
						progressExp = 0;
					} else {
						progressExp = (int) taskProgress.getProgress();
					}
					
					taskProgress.setProgress(progressExp + amount);
					
					if (((int) taskProgress.getProgress()) >= expNeeded) {
						taskProgress.setCompleted(true);
					}					
				}
			}
		}		
	}
}
