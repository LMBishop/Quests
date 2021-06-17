package com.leonardobishop.quests.bukkit;

import com.leonardobishop.quests.common.scheduler.ServerScheduler;

public class BukkitServerSchedulerAdapter implements ServerScheduler {

    private final BukkitQuestsPlugin plugin;

    public BukkitServerSchedulerAdapter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void doSync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void doAsync(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }
}
