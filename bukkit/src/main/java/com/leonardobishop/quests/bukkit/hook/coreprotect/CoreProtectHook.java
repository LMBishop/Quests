package com.leonardobishop.quests.bukkit.hook.coreprotect;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.plugin.Quests;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CoreProtectHook implements AbstractCoreProtectHook {

    private final BukkitQuestsPlugin plugin;
    private final CoreProtectAPI api;

    public CoreProtectHook(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        api = ((CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect")).getAPI();
    }

    @Override
    public CompletableFuture<Boolean> checkBlock(Block block, int time) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getScheduler().doAsync(() -> {
            List<String[]> lookup = api.blockLookup(block, time);
            if (lookup.isEmpty()) {
                plugin.getScheduler().doSync(() -> future.complete(false));
            } else {
                String[] result = lookup.get(0);
                CoreProtectAPI.ParseResult parseResult = api.parseResult(result);
                boolean value = !parseResult.getPlayer().isEmpty() && parseResult.getActionId() == 1;

                plugin.getScheduler().doSync(() -> future.complete(value));
            }
        });
        return future;
    }

}
