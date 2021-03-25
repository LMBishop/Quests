package com.leonardobishop.quests.hooks.coreprotect;

import org.bukkit.block.Block;

public class CoreProtectNoHook implements ICoreProtectHook {
    @Override
    public boolean checkBlock(Block block, int time) {
        return false;
    }
}
