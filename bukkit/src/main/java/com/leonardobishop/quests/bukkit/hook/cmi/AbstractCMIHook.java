package com.leonardobishop.quests.bukkit.hook.cmi;

import org.bukkit.entity.Player;

public interface AbstractCMIHook {
    /**
     * Check whether or not the passed player is marked AFK by CMI
     *
     * @param player the block
     * @return true if afk, false otherwise
     */
    boolean isAfk(Player player);
}
