package com.leonardobishop.quests.bukkit.hook.bossbar;

import org.bukkit.entity.Player;

public class BossBar_Nothing implements QuestsBossBar {

    @Override
    public void sendBossBar(Player player, String questId, String title, int time) {
        // no compatible boss bar impl found
    }

    @Override
    public void sendBossBar(Player player, String questId, String title, int time, float progress) {
        // no compatible boss bar impl found
    }
}
