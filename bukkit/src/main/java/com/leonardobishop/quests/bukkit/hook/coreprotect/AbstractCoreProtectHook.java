package com.leonardobishop.quests.bukkit.hook.coreprotect;

import org.bukkit.block.Block;

public interface AbstractCoreProtectHook {

    /**
     * Check whether or not the most recent edit to a block was the result of a player.
     *
     * @param block the block
     * @param time the time to look back in seconds
     * @return true if from a player
     */
    boolean checkBlock(Block block, int time);

}
