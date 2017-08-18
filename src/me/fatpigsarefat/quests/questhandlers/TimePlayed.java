package me.fatpigsarefat.quests.questhandlers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestType;

public class TimePlayed extends BukkitRunnable {

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			ArrayList<Quest> completedQuests = new ArrayList<Quest>();
			
			Quests.getInstance().getQuestData().setTimePlayed(player.getUniqueId(),
					Quests.getInstance().getQuestData().getTimePlayed(player.getUniqueId()) + 1);

			for (Quest q : Quests.getInstance().getQuestManager().getQuestsByType(QuestType.TIMEPLAYED)) {
				if (Quests.getInstance().getQuestData().hasStartedQuest(q, player.getUniqueId())) {
					if (Quests.getInstance().getQuestData().getTimePlayed(player.getUniqueId()) >= Quests.getInstance()
							.getQuestData().parseTimeplayedValue(q)) {
						completedQuests.add(q);
					}
				}
			}
			
			for (Quest q : completedQuests) {
				Quests.getInstance().getQuestData().completeQuest(q, player.getUniqueId());
			}
		}
	}
}