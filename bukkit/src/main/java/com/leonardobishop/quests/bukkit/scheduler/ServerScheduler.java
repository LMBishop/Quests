package com.leonardobishop.quests.bukkit.scheduler;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ServerScheduler extends com.leonardobishop.quests.common.scheduler.ServerScheduler {

    /**
     * Cancel all tasks related to this server.
     */
    void cancelAllTasks();

    /**
     * Cancel the provided wrapped task.
     *
     * @param wrappedTask Wrapped task to cancel.
     */
    void cancelTask(@NotNull WrappedTask wrappedTask);

    /**
     * Run a new task timer.
     * <p>
     * Folia: Synced with the server daylight cycle tick.
     * <p>
     * Paper: Synced with the server main thread.
     *
     * @param runnable Runnable to run.
     * @param delay Delay before first execution. Must be greater than zero.
     * @param period Delay between executions. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    WrappedTask runTaskTimer(@NotNull Runnable runnable, long delay, long period);

    /**
     * Run a new task timer asynchronously.
     * <p>
     * Folia: Run in the dedicated async thread.
     * <p>
     * Paper: Run in the dedicated async thread.
     *
     * @param runnable Runnable to run.
     * @param delay Delay before first execution. Must be greater than zero.
     * @param period Delay between executions. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    WrappedTask runTaskTimerAsynchronously(@NotNull Runnable runnable, long delay, long period);

    /**
     * Run a new task later.
     * <p>
     * Folia: Synced with the server daylight cycle tick.
     * <p>
     * Paper: Synced with the server main thread.
     *
     * @param runnable Runnable to run.
     * @param delay Delay before first execution. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    WrappedTask runTaskLater(@NotNull Runnable runnable, long delay);

    /**
     * Run a new task later asynchronously.
     * <p>
     * Folia: Run in the dedicated async thread.
     * <p>
     * Paper: Run in the dedicated async thread.
     *
     * @param runnable Runnable to run.
     * @param delay Delay before first execution. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    WrappedTask runTaskLaterAsynchronously(@NotNull Runnable runnable, long delay);

    /**
     * Run a new task later.
     * <p>
     * Folia: Synced with the tick of the region of the entity (even if the entity moves).
     * <p>
     * Paper: Synced with the server main thread.
     *
     * @param runnable Runnable to run.
     * @param delay Delay before first execution. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    WrappedTask runTaskLaterAtEntity(@NotNull Entity entity, @NotNull Runnable runnable, long delay);

    /**
     * Get the server scheduler name.
     *
     * @return {@link String} server scheduler name.
     */
    @NotNull
    default String getServerSchedulerName() {
        return getClass().getSimpleName();
    }
}
