package com.leonardobishop.quests.bukkit.hook.bossbar;

import org.bukkit.entity.Player;

public class BossBar_Nothing implements BossBar {

	@Override
	public void sendBossBar(Player p, String questKey, String title, int time) {
		// old versions as 1.8
	}

	@Override
	public void sendBossBar(Player p, String questKey, String title, int progress, int time) {
		// old versions as 1.8
	}

}
