package com.leonardobishop.quests.bukkit.scheduler.bukkit;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.ServerScheduler;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class BukkitServerSchedulerAdapter implements ServerScheduler {

    private final BukkitQuestsPlugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public BukkitServerSchedulerAdapter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.bukkitScheduler = plugin.getServer().getScheduler();
    }

    @Override
    public void cancelAllTasks() {
        bukkitScheduler.cancelTasks(plugin);
    }

    @Override
    public void cancelTask(@NotNull WrappedTask wrappedTask) {
        wrappedTask.cancel();
    }

    @Override
    public @NotNull WrappedTask runTask(@NotNull Runnable runnable) {
        return new BukkitWrappedTask(bukkitScheduler.runTask(plugin, runnable));
    }

    @Override
    public @NotNull WrappedTask runTaskAsynchronously(@NotNull Runnable runnable) {
        return new BukkitWrappedTask(bukkitScheduler.runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public @NotNull WrappedTask runTaskAtEntity(@NotNull Entity entity, @NotNull Runnable runnable) {
        return runTask(runnable);
    }

    @Override
    public @NotNull WrappedTask runTaskAtLocation(@NotNull Location location, @NotNull Runnable runnable) {
        return runTask(runnable);
    }

    @Override
    public @NotNull WrappedTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        return new BukkitWrappedTask(bukkitScheduler.runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public @NotNull WrappedTask runTaskTimerAsynchronously(@NotNull Runnable runnable, long delay, long period) {
        return new BukkitWrappedTask(bukkitScheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period));
    }

    @Override
    public @NotNull WrappedTask runTaskTimerAtEntity(@NotNull Entity entity, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    @Override
    public @NotNull WrappedTask runTaskTimerAtLocation(@NotNull Location location, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    @Override
    public @NotNull WrappedTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return new BukkitWrappedTask(bukkitScheduler.runTaskLater(plugin, runnable, delay));
    }

    @Override
    public @NotNull WrappedTask runTaskLaterAsynchronously(@NotNull Runnable runnable, long delay) {
        return new BukkitWrappedTask(bukkitScheduler.runTaskLaterAsynchronously(plugin, runnable, delay));
    }

    @Override
    public @NotNull WrappedTask runTaskLaterAtEntity(@NotNull Entity entity, @NotNull Runnable runnable, long delay) {
        return runTaskLater(runnable, delay);
    }

    @Override
    public @NotNull WrappedTask runTaskLaterAtLocation(@NotNull Location location, @NotNull Runnable runnable, long delay) {
        return runTaskLater(runnable, delay);
    }

    @Override
    public void doSync(Runnable runnable) {
        bukkitScheduler.runTask(plugin, runnable);
    }

    @Override
    public void doAsync(Runnable runnable) {
        bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }
}
