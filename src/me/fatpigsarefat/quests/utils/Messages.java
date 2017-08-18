package me.fatpigsarefat.quests.utils;

import org.bukkit.ChatColor;

import me.fatpigsarefat.quests.Quests;

public enum Messages {

	STARTED_QUEST(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.started-quest"))),
	COMPLETE_QUEST(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.complete-quest"))),
	REWARDS(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.rewards"))),
	REWARD_STRING_FORMAT(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.reward-string-format"))),
	SYNC_PLAYTIME(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.sync-playtime"))),
	SYNC_TIMEOUT(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.sync-timeout"))),
	SYNC_SUCCESS(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.sync-success"))),
	QUESTS_REFRESHED(ChatColor.translateAlternateColorCodes('&', Quests.getInstance().getConfig().getString("messages.quests-refresh")));
	
	private String message;
	
	private Messages(String st) {
		message = st;
	}
	
	public String getMessage() {
		return message;
	}
	
}
