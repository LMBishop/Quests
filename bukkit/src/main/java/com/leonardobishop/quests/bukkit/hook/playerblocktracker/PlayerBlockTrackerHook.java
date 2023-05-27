package com.leonardobishop.quests.bukkit.hook.playerblocktracker;

import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;

public class PlayerBlockTrackerHook implements AbstractPlayerBlockTrackerHook {

    private final Class<? extends Plugin> pluginClazz;
    private final Method isTrackedMethod;

    public PlayerBlockTrackerHook(Class<? extends Plugin> pluginClazz, Method isTrackedMethod) {
        this.pluginClazz = pluginClazz;
        this.isTrackedMethod = isTrackedMethod;
    }

    @Override
    public boolean checkBlock(Block block) {
        try {
            return (boolean) this.isTrackedMethod.invoke(null, block);
        } catch (Throwable e) { // suppress all errors and exceptions
            return false;
        }
    }

    @Override
    public void fixPlayerBlockTracker() {
        HandlerList handlerList = BlockBreakEvent.getHandlerList();
        RegisteredListener[] listeners = handlerList.getRegisteredListeners();
        for (RegisteredListener listener : listeners) {
            if (listener.getPlugin().getClass() == this.pluginClazz && listener.getPriority() == EventPriority.MONITOR) {
                handlerList.unregister(listener);
                handlerList.register(listener);
            }
        }
        handlerList.bake();
    }
}
