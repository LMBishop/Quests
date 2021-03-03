package com.leonardobishop.quests.hooks.coreprotect;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.List;

public class CoreProtectHook implements ICoreProtectHook {

    private final CoreProtectAPI api;

    public CoreProtectHook() {
        api = ((CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect")).getAPI();
    }

    @Override
    public boolean checkBlock(Block block, int time) {
        List<String[]> lookup = api.blockLookup(block, time);
        if (lookup.isEmpty()) return false;

        String[] result = lookup.get(0);
        CoreProtectAPI.ParseResult parseResult = api.parseResult(result);

        return !parseResult.getPlayer().isEmpty() && parseResult.getActionId() == 1;
    }

}
