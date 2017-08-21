package me.fatpigsarefat.quests.questhandlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestType;
import me.fatpigsarefat.quests.utils.QuestUtil;

public class MobKill implements Listener {

	public MobKill(Quests plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMobKill(EntityDeathEvent event) {
		Entity killer = event.getEntity().getKiller();
		Entity mob = event.getEntity();

		if (mob instanceof Player) {
			return;
		}

		if (!(killer instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity().getKiller();
		
		ArrayList<Quest> mobkillingQuests = Quests.getInstance().getQuestManager().getQuestsByType(QuestType.MOBKILLING);
		ArrayList<Quest> mobkillingCertainQuests = Quests.getInstance().getQuestManager().getQuestsByType(QuestType.MOBKILLINGCERTAIN);
		List<String> questsStarted = Quests.getInstance().getQuestData().getStartedQuests(player.getUniqueId());
		ArrayList<Quest> questsToAddValue = new ArrayList<Quest>();

		if (mobkillingQuests.isEmpty() && mobkillingCertainQuests.isEmpty()) {
			return;
		}
		
		if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
			return;
		}

		for (Quest quest : mobkillingQuests) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questsStarted.contains(quest.getNameId())) {
				questsToAddValue.add(quest);
			}
		}

		for (Quest quest : mobkillingCertainQuests) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questsStarted.contains(quest.getNameId())) {
				EntityType entityType = QuestUtil.parseMobkillingCertainValue(quest).getEntity();
				EntityType killedMob = mob.getType();
				if (entityType == killedMob) {
					questsToAddValue.add(quest);
				}
			}
		}

		if (questsToAddValue.isEmpty()) {
			return;
		}

		for (Quest quest : questsToAddValue) {
			Quests.getInstance().getQuestData().addProgress(quest, player.getUniqueId());
			if (quest.getQuestType() == QuestType.MOBKILLING) {
				if (Quests.getInstance().getQuestData().getProgress(quest, player.getUniqueId()) >= QuestUtil.parseMobkillingValue(quest)) {
					Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
				}
			} else if (quest.getQuestType() == QuestType.MOBKILLINGCERTAIN) {
				if (Quests.getInstance().getQuestData().getProgress(quest, player.getUniqueId()) >= QuestUtil.parseMobkillingCertainValue(quest).getNeededToKill()) {
					Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
				}
			}
		}
	}
}
