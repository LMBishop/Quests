package me.fatpigsarefat.quests.questhandlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestType;

public class BlockPlace implements Listener {


	public BlockPlace(Quests plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		ArrayList<Quest> buildingQuests = Quests.getInstance().getQuestManager().getQuestsByType(QuestType.BUILDING);
		ArrayList<Quest> buildingCertainQuests = Quests.getInstance().getQuestManager().getQuestsByType(QuestType.BUILDINGCERTAIN);
		List<String> questsStarted = Quests.getInstance().getQuestData().getStartedQuests(player.getUniqueId());
		ArrayList<Quest> questsToAddValue = new ArrayList<Quest>();

		if (buildingQuests.isEmpty() && buildingCertainQuests.isEmpty()) {
			return;
		}
		
		if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
			return;
		}

		for (Quest quest : buildingQuests) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questsStarted.contains(quest.getNameId())) {
				if (Quests.getInstance().getConfig().getBoolean("quest-settings.building.exclude-non-solid-blocks")) {
					if (!event.getBlock().getType().isSolid()) {
						continue;
					}
				}
				questsToAddValue.add(quest);
			}
		}

		for (Quest quest : buildingCertainQuests) {
			if (quest.isWorldsRestriced()) {
				if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
					continue;
				}
			}
			if (questsStarted.contains(quest.getNameId())) {
				int idToCheck = Quests.getInstance().getQuestData().parseBuildingCertainValue(quest).getId();
				String blockName = event.getBlock().getType().toString();
				if (Quests.getInstance().getAlternateNamesForBlocks().containsKey(event.getBlock().getType().toString())) {
					blockName = Material.getMaterial(Quests.getInstance().getAlternateNamesForBlocks().get(event.getBlock().getType().toString())).toString();
				}
				if (Material.getMaterial(idToCheck).toString().equals(blockName)) {
					questsToAddValue.add(quest);
				}
			}
		}

		if (questsToAddValue.isEmpty()) {
			return;
		}

		for (Quest quest : questsToAddValue) {
			Quests.getInstance().getQuestData().addProgress(quest, player.getUniqueId());
			if (quest.getQuestType() == QuestType.BUILDING) {
				if (Quests.getInstance().getQuestData().getProgress(quest, player.getUniqueId()) >= Quests.getInstance().getQuestData().parseBuildingValue(quest)) {
					Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
				}
			} else if (quest.getQuestType() == QuestType.BUILDINGCERTAIN) {
				if (Quests.getInstance().getQuestData().getProgress(quest, player.getUniqueId()) >= Quests.getInstance().getQuestData().parseBuildingCertainValue(quest).getValue()) {
					Quests.getInstance().getQuestData().completeQuest(quest, player.getUniqueId());
				}
			}
		}
	}
}
