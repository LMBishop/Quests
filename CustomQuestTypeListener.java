package me.fatpigsarefat.base.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestData;
import me.fatpigsarefat.quests.utils.QuestManager;
import me.fatpigsarefat.quests.utils.QuestType;

public class EventPlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerMoveEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
			if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

				// Get the quest manager which stores all the quests
				QuestManager questManager = Quests.getInstance().getQuestManager();

				// Check if the player is not in a blacklisted world.
				if (questManager.getBlacklistedWorlds().contains(event.getPlayer().getWorld().getName())) {
					return;
				}

				// Get the quest data class which does all things related to the
				// data file e.g quest progress
				QuestData questData = Quests.getInstance().getQuestData();

				// Get all quests by custom type "WALKING"
				for (Quest quest : questManager.getQuestsByCustomType("WALKING")) {

					// Check if player has started the quest, if not skip
					if (!questData.getStartedQuests(event.getPlayer().getUniqueId()).contains(quest.getNameId())) {
						continue;
					}
					
					// The check if the quest is world-restricted
					if (quest.isWorldsRestriced()) {
						if (!quest.getAllowedWorlds().contains(event.getPlayer().getWorld().getName())) {
							continue;
						}
					}
					
					// Add progress to the player
					questData.addProgress(quest, event.getPlayer().getUniqueId());

					// Parse the completion value in the quest (might want to
					// catch a NumberFormatException here) and check if the
					// player has met the conditions to complete the quest
					if (questData.getProgress(quest, event.getPlayer().getUniqueId()) >= Integer
							.parseInt(quest.getCompletionValue())) {
						// If so, complete the quest and give out the rewards
						questData.completeQuest(quest, event.getPlayer().getUniqueId());
					}
				}
			}
		}
	}

	// This shows how to register a quest programatically
	private void registerQuestProgramatically() {
		// The parent node in the config
		String questId = "codeblockbreak1";

		// Known as 'type' in the config. To make it a custom type, set the
		// QuestType to CUSTOM
		QuestType questType = QuestType.MINING;

		// Known as 'custom-type' in the config. This identifies the type for
		// custom quests.
		String customName = "";

		// Known as 'redoable' in the config.
		boolean redoable = true;

		// Known as 'cooldown.enabled' in the config.
		boolean cooldownEnabled = true;

		// Known as 'cooldown.minutes' in the config.
		int cooldown = 30;

		// Known as 'requirements' in the config.
		ArrayList<String> requirements = new ArrayList<String>();

		// Known as 'value' in the config, the quest data class contains methods
		// which will parse this string for official quests only, for custom
		// quests you'll have to do this yourself
		String completionValue = "20";

		// Known as 'rewards' in the config.
		ArrayList<String> rewards = new ArrayList<String>(Arrays.asList("type:command, value:[eco give %player% 500]"));

		// Known as 'rewardString' in the config.
		ArrayList<String> rewardString = new ArrayList<String>(Arrays.asList("&a$500 added to your balance."));

		// Known as 'display.*' in the config. It's just an ordinary ItemStack.
		// Note: No two quests can have the same display name.
		ItemStack displayItem = new ItemStack(Material.COBBLESTONE);
		ItemMeta displayItemM = displayItem.getItemMeta();
		displayItemM.setDisplayName(ChatColor.BLUE + "Block Break I (Registered in code)");
		List<String> lore = (List<String>) Arrays.asList(ChatColor.GRAY + "Mine 20 blocks.", ChatColor.GRAY + "Progress: %progress% blocks", ChatColor.GRAY +  "This quest was registered in code.");
		displayItemM.setLore(lore);
		displayItem.setItemMeta(displayItemM);

		// Known as 'worlds.restricted' in the config.
		boolean worldsRestricted = false;

		// Known as 'worlds.allowed-worlds' in the config.
		ArrayList<String> allowedWorlds = new ArrayList<String>();

		Quest quest = new Quest(questType, questId, displayItem, redoable, cooldownEnabled, cooldown, rewards,
				rewardString, requirements, completionValue, worldsRestricted, allowedWorlds, customName);
		
		// Get the quest manager which stores all the quests
		QuestManager questManager = Quests.getInstance().getQuestManager();
		
		questManager.registerQuest(quest);
	}
}
