package com.leonardobishop.quests.bukkit.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapped task for the scheduler.
 */
public interface WrappedTask {

    /**
     * Cancel the task.
     */
    void cancel();

    /**
     * Check if the task is cancelled.
     *
     * @return {@code true} if it is cancelled, {@code false} otherwise.
     */
    boolean isCancelled();

    /**
     * Get the owning plugin for this task.
     *
     * @return {@link Plugin} plugin.
     */
    @NotNull
    Plugin getOwningPlugin();
}
