package me.fatpigsarefat.quests.questhandlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestType;
import me.fatpigsarefat.quests.utils.QuestUtil;

public class PlayerKill implements Listener {

	public PlayerKill(Quests plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKill(EntityDeathEvent event) {
		Entity killer = event.getEntity().getKiller();
		Entity mob = event.getEntity();

		if (!(mob instanceof Player)) {
			return;
		}

		if (!(killer instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity().getKiller();

		ArrayList<Quest> playerKillingQuests = Quests.getInstance().getQuestManager()
				.getQuestsByType(QuestType.PLAYERKILLING);
		List<String> questsStarted = Quests.getInstance().getQuestData().getStartedQuests(player.getUniqueId());
		ArrayList<Quest> questsToAddValue = new ArrayList<Quest>();

		if (playerKillingQuests.isEmpty()) {
			return;
		}
		
		if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
			return;
		}

		for (Quest quest : playerKillingQuests) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questsStarted.contains(quest.getNameId())) {
				questsToAddValue.add(quest);
			}
		}

		if (questsToAddValue.isEmpty()) {
			return;
		}

		for (Quest quest : questsToAddValue) {
			Quests.getInstance().getQuestData().addProgress(quest, player.getUniqueId());
			if (Quests.getInstance().getQuestData().getProgress(quest, player.getUniqueId()) >= QuestUtil.parsePlayerkillingValue(quest)) {
				Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
			}
		}
	}
}
