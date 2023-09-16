package com.leonardobishop.quests.bukkit.hook.title;

import org.bukkit.entity.Player;

public class Title_Nothing implements QuestsTitle {
    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        // no compatible title impl found
    }
}
