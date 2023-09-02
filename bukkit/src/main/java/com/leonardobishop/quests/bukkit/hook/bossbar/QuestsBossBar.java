package com.leonardobishop.quests.bukkit.hook.bossbar;

import org.bukkit.entity.Player;

public interface QuestsBossBar {

    void sendBossBar(Player player, String questId, String title, int time);

    void sendBossBar(Player player, String questId, String title, int time, float progress);

}
