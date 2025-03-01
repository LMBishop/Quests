package com.leonardobishop.quests.bukkit.scheduler.bukkit;

import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Predicate;

public class BukkitWrappedTask implements WrappedTask {

    // TODO: Replace this reflection shit with a more proper solution
    private static final Predicate<BukkitTask> isCancelledPredicate;

    static {
        final Predicate<BukkitTask> predicate;

        Method isCancelledMethod;
        try {
            isCancelledMethod = BukkitTask.class.getMethod("isCancelled");
        } catch (final NoSuchMethodException e) {
            isCancelledMethod = null; // doesn't exist on 1.8
        }

        if (isCancelledMethod != null) {
            predicate = BukkitTask::isCancelled;
        } else {
            predicate = task -> {
                final int taskId = task.getTaskId();
                final BukkitScheduler scheduler = Bukkit.getScheduler();
                return !(scheduler.isCurrentlyRunning(taskId) || scheduler.isQueued(taskId));
            };
        }

        isCancelledPredicate = predicate;
    }
    // TODO: Replace this reflection shit with a more proper solution

    private final BukkitTask bukkitTask;

    public BukkitWrappedTask(@NotNull BukkitTask bukkitTask) {
        this.bukkitTask = Objects.requireNonNull(bukkitTask);
    }

    @Override
    public void cancel() {
        bukkitTask.cancel();
    }

    @Override
    public boolean isCancelled() {
        return isCancelledPredicate.test(bukkitTask);
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return bukkitTask.getOwner();
    }
}
