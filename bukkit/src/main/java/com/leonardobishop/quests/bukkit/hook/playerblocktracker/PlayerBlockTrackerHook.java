package com.leonardobishop.quests.bukkit.hook.playerblocktracker;

import com.gestankbratwurst.playerblocktracker.PlayerBlockTracker;
import org.bukkit.block.Block;

public class PlayerBlockTrackerHook implements AbstractPlayerBlockTrackerHook {

    @Override
    public boolean checkBlock(Block block) {
        return PlayerBlockTracker.isTracked(block);
    }

}
