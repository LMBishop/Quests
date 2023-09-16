package com.leonardobishop.quests.bukkit.hook.actionbar;

import org.bukkit.entity.Player;

public class ActionBar_Paper implements QuestsActionBar {
    @SuppressWarnings("deprecation")
    @Override
    public void sendActionBar(Player player, String title) {
        player.sendActionBar(title);
    }
}
