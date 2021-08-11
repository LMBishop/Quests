package com.leonardobishop.quests.bukkit.hook.essentials;

import org.bukkit.entity.Player;

public interface AbstractEssentialsHook {
    /**
     * Check whether or not the passed player is marked AFK by Essentials
     *
     * @param player the block
     * @return true if afk, false otherwise
     */
    boolean isAfk(Player player);
}
