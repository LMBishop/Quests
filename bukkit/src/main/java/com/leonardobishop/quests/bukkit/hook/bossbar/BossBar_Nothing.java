package com.leonardobishop.quests.bukkit.hook.bossbar;

import org.bukkit.entity.Player;

public class BossBar_Nothing implements QuestsBossBar {

    @Override
    public void sendBossBar(Player player, String questId, String title, int time) {
        // old versions as 1.8
    }

    @Override
    public void sendBossBar(Player player, String questId, String title, int time, float progress) {
        // old versions as 1.8
    }

}
