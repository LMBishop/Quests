package com.leonardobishop.quests.hooks.title;

import org.bukkit.entity.Player;

public class Title_Bukkit implements Title {

    // new title function with timings
    @Override
    public void sendTitle(Player player, String message, String submessage) {
        player.sendTitle(message, submessage, 10, 100, 10);
    }
}
