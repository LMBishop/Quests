package com.leonardobishop.quests.bukkit.hook.title;

import org.bukkit.entity.Player;

public interface Title {

    void sendTitle(Player player, String message, String submessage);
}