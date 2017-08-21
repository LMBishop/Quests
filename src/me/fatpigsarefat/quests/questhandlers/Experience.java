package me.fatpigsarefat.quests.questhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestData;
import me.fatpigsarefat.quests.utils.QuestManager;
import me.fatpigsarefat.quests.utils.QuestType;
import me.fatpigsarefat.quests.utils.QuestUtil;

public class Experience implements Listener {

	public Experience(Quests plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKill(PlayerExpChangeEvent event) {
		float experience = event.getPlayer().getLevel();
		float totalExperience = event.getPlayer().getTotalExperience();
		Player player = event.getPlayer();

		QuestManager questManager = Quests.getInstance().getQuestManager();
		QuestData questData = Quests.getInstance().getQuestData();
		
		if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
			return;
		}

		for (Quest quest : questManager.getQuestsByType(QuestType.EXP)) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questData.getStartedQuests(player.getUniqueId()).contains(quest.getNameId())) {
				if (experience >= QuestUtil.parseExperienceValue(quest)) {
					questData.completeQuest(quest, player.getUniqueId());
				}
			}
		}
		
		for (Quest quest : questManager.getQuestsByType(QuestType.TOTALEXP)) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questData.getStartedQuests(player.getUniqueId()).contains(quest.getNameId())) {
				if (totalExperience >= QuestUtil.parseExperienceValue(quest)) {
					questData.completeQuest(quest, player.getUniqueId());
				}
			}
		}
	}
}
