package me.fatpigsarefat.quests.questhandlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestData;
import me.fatpigsarefat.quests.utils.QuestManager;
import me.fatpigsarefat.quests.utils.QuestType;
import me.fatpigsarefat.quests.utils.QuestUtil;

public class Walking implements Listener {

	public Walking(Quests plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerWalk(PlayerMoveEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
			if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
				QuestManager questManager = Quests.getInstance().getQuestManager();
				if (questManager.getBlacklistedWorlds().contains(event.getPlayer().getWorld().getName())) {
					return;
				}
				QuestData questData = Quests.getInstance().getQuestData();

				for (Quest quest : questManager.getQuestsByType(QuestType.WALKING)) {
					if (!questData.getStartedQuests(event.getPlayer().getUniqueId()).contains(quest.getNameId())) {
						continue;
					}
					if (quest.isWorldsRestriced()) {
						if (!quest.getAllowedWorlds().contains(event.getPlayer().getWorld().getName())) {
							continue;
						}
					}
					
					questData.addProgress(quest, event.getPlayer().getUniqueId());
					if (questData.getProgress(quest, event.getPlayer().getUniqueId()) >= QuestUtil.parseWalkingValue(quest)) {
						questData.completeQuest(quest, event.getPlayer().getUniqueId());
					}
				}
			}
		}
	}
}
