package com.leonardobishop.quests.bukkit.hook.playerblocktracker;

import org.bukkit.block.Block;

public interface AbstractPlayerBlockTrackerHook {

    /**
     * Check whether or not the most recent edit to a block was the result of a player.
     *
     * @param block the block
     * @return true if from a player
     */
    boolean checkBlock(Block block);

    /**
     * Changes PlayerBlockTracker listener to be called after the Quests' one is
     */
    void fixPlayerBlockTracker();

}
