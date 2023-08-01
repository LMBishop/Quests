package com.leonardobishop.quests.bukkit.hook.bossbar;

import org.bukkit.entity.Player;

public interface BossBar {

	void sendBossBar(Player p, String questKey, String title, int time);

	void sendBossBar(Player p, String questKey, String title, int progress, int time);
	
}
