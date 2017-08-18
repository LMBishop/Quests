package me.fatpigsarefat.quests.utils;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.fatpigsarefat.quests.Quests;

public class ExternalPlaceholders {

	public static void register() {
		PlaceholderAPI.registerPlaceholder(Quests.getInstance(), "fatpigsarefat:quests_complete", new PlaceholderReplacer() {

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return String.valueOf(Quests.getInstance().getQuestData().getAmountOfCompletedQuests(event.getOfflinePlayer().getUniqueId()));
			}
			
		});
	}
	
}
