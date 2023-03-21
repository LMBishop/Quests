package com.leonardobishop.quests.bukkit.hook.playerblocktracker;

import com.gestankbratwurst.playerblocktracker.PlayerBlockTracker;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.RegisteredListener;

public class PlayerBlockTrackerHook implements AbstractPlayerBlockTrackerHook {

    @Override
    public boolean checkBlock(Block block) {
        return PlayerBlockTracker.isTracked(block);
    }

    @Override
    public void fixPlayerBlockTracker() {
        PlayerBlockTracker playerBlockTracker = (PlayerBlockTracker) Bukkit.getPluginManager().getPlugin("PlayerBlockTracker");
        HandlerList handlerList = BlockBreakEvent.getHandlerList();
        RegisteredListener[] listeners = handlerList.getRegisteredListeners();
        for (RegisteredListener listener : listeners) {
            if (listener.getPlugin() == playerBlockTracker && listener.getPriority() == EventPriority.MONITOR) {
                handlerList.unregister(listener);
                handlerList.register(listener);
            }
        }
        handlerList.bake();
    }
}
