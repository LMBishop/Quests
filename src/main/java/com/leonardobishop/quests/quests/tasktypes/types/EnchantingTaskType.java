package com.leonardobishop.quests.quests.tasktypes.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;

public final class EnchantingTaskType extends TaskType {
	
	private List<ConfigValue> creatorConfigValues = new ArrayList<>();
	
	public EnchantingTaskType() {
		super("enchanting", "toasted", "Enchant a certain amount of items.");
		this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of items you need to enchant."));
	}
	
	@Override
	public List<ConfigValue> getCreatorConfigValues() {
		return creatorConfigValues;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent e) {
		Player player = e.getEnchanter();
		
		QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
		QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
		
		for (Quest quest : super.getRegisteredQuests()) {
			if (questProgressFile.hasStartedQuest(quest)) {
				QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
				
				for (Task task : quest.getTasksOfType(super.getType())) {
					TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
					
					if (taskProgress.isCompleted()) {
						continue;
					}
					
					int enchantsNeeded = (int) task.getConfigValue("amount");
					
					int progressEnchant;
					if (taskProgress.getProgress() == null) {
						progressEnchant = 0;
					} else {
						progressEnchant = (int) taskProgress.getProgress();
					}
					
					taskProgress.setProgress(progressEnchant + 1);
					
					if (((int) taskProgress.getProgress()) >= enchantsNeeded) {
						taskProgress.setCompleted(true);
					}					
				}	
			}			
		}
	}
}
