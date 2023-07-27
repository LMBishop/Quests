package com.leonardobishop.quests.bukkit.scheduler;

import org.jetbrains.annotations.NotNull;

/**
 * Wrapped runnable.
 */
public abstract class WrappedRunnable implements Runnable {

    private WrappedTask wrappedTask;

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized boolean isCancelled() throws IllegalStateException {
        checkScheduled();
        return wrappedTask.isCancelled();
    }

    /**
     * Run a new task timer.
     * <p>
     * Folia: Synced with the server daylight cycle tick.
     * <p>
     * Paper: Synced with the server main thread.
     *
     * @param delay Delay before first execution. Must be greater than zero.
     * @param period Delay between executions. Must be greater than zero.
     * @return {@link WrappedTask} task reference.
     */
    @NotNull
    public WrappedTask runTaskTimer(@NotNull ServerScheduler serverScheduler, long delay, long period) {
        checkNotYetScheduled();
        return setupTask(serverScheduler.runTaskTimer(this, delay, period));
    }

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        wrappedTask.cancel();
    }

    private void checkScheduled() {
        if (wrappedTask == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (wrappedTask != null) {
            throw new IllegalStateException("Task is already scheduled!");
        }
    }

    @NotNull
    private WrappedTask setupTask(@NotNull final WrappedTask wrappedTask) {
        this.wrappedTask = wrappedTask;
        return wrappedTask;
    }
}
