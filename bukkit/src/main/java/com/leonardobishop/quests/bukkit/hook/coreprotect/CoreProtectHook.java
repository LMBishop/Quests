package com.leonardobishop.quests.bukkit.hook.coreprotect;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CoreProtectHook implements AbstractCoreProtectHook {

    private final BukkitQuestsPlugin plugin;
    private final CoreProtectAPI api;

    public CoreProtectHook(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.api = CoreProtect.getInstance().getAPI();
    }

    @Override
    public CompletableFuture<Boolean> checkBlock(Block block, int time) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getScheduler().doAsync(() -> {
            List<String[]> queueLookup = api.queueLookup(block);

            if (queueLookup.size() >= 2) {
                // first queue element when breaking a block is always
                // a break action so we just get the second one
                String[] result = queueLookup.get(1);
                CoreProtectAPI.ParseResult parseResult = api.parseResult(result);

                // queue lookup returns only break and place actions
                // so we dont need to skip all the interations (action id 2)
                // https://github.com/PlayPro/CoreProtect/blob/master/src/main/java/net/coreprotect/api/QueueLookup.java#L55
                if (!parseResult.getPlayer().isEmpty() && parseResult.getActionId() == 1) {
                    plugin.getScheduler().doSync(() -> future.complete(true));
                    return;
                }
            }

            long blockLookupDelay = plugin.getConfig().getLong("options.coreprotect-block-lookup-delay", -1L);
            if (blockLookupDelay > 0L) {
                try {
                    Thread.sleep(blockLookupDelay);
                } catch (InterruptedException ignored) {
                }
            }

            List<String[]> blockLookup = api.blockLookup(block, time);

            if (blockLookup == null) {
                plugin.getLogger().severe("CoreProtect block lookup returned null! Please ensure, that the CoreProtect API is enabled in its config.");

                plugin.getScheduler().doSync(() -> future.complete(true));
                return;
            }

            boolean first = true;

            for (String[] result : blockLookup) {
                CoreProtectAPI.ParseResult parseResult = api.parseResult(result);

                // we need to skip all the interations like door opening (action id 2)
                if (parseResult.getActionId() == 2) {
                    continue;
                }

                // we need to skip first break interaction too in case it's already been inserted
                if (first && parseResult.getActionId() == 0) {
                    first = false;

                    continue;
                }

                // due to the order the first element that is not
                // an interaction is always the one we need to check
                // (0=removed, 1=placed, 2=interaction)
                // https://docs.coreprotect.net/api/version/v9/#parseresult-parseresultstring-result
                boolean placed = parseResult.getActionId() == 1;

                plugin.getScheduler().doSync(() -> future.complete(placed));
                return;
            }

            plugin.getScheduler().doSync(() -> future.complete(false));
        });
        return future;
    }
}
