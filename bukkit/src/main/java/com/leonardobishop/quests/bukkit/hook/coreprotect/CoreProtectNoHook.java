package com.leonardobishop.quests.bukkit.hook.coreprotect;

import org.bukkit.block.Block;

public class CoreProtectNoHook implements AbstractCoreProtectHook {
    @Override
    public boolean checkBlock(Block block, int time) {
        return false;
    }
}
