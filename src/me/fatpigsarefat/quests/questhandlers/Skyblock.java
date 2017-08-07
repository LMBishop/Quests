package me.fatpigsarefat.quests.questhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestType;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

public class Skyblock extends BukkitRunnable {

	public void run() {
		if (Quests.getInstance().isAskyblockEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
				for (Quest quest : Quests.getInstance().getQuestManager().getQuestsByType(QuestType.ASKYBLOCK)) {
					if (!Quests.getInstance().getQuestData().hasStartedQuest(quest, player.getUniqueId())) {
						continue;
					}
					if (ASkyBlockAPI.getInstance().getLongIslandLevel(player.getUniqueId()) >= Quests.getInstance()
							.getQuestData().parseAskyblockValue(quest)) {
						Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
					}
				}
			}
		}
		if (Quests.getInstance().isUskyblockEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
				for (Quest quest : Quests.getInstance().getQuestManager().getQuestsByType(QuestType.USKYBLOCK)) {
					if (!Quests.getInstance().getQuestData().hasStartedQuest(quest, player.getUniqueId())) {
						continue;
					}
					Plugin plugin = Bukkit.getPluginManager().getPlugin("uSkyBlock");
					uSkyBlockAPI usb = (uSkyBlockAPI) plugin;
					if (usb.getIslandLevel(player) >= Quests.getInstance()
							.getQuestData().parseAskyblockValue(quest)) {
						Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
					}
				}
			}
		}
	}
}