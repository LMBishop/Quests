package com.leonardobishop.quests.hooks.title;

import org.bukkit.entity.Player;

public class Title_BukkitNoTimings implements Title {

    // this one is for 1.8, 1.9 and 1.10 where there was no timings method
    @Override
    public void sendTitle(Player player, String message, String submessage) {
        player.sendTitle(message, submessage);
    }

}
