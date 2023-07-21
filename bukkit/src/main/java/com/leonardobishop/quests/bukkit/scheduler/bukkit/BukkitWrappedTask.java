package com.leonardobishop.quests.bukkit.scheduler.bukkit;

import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitWrappedTask implements WrappedTask {

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
        return bukkitTask.isCancelled();
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return bukkitTask.getOwner();
    }
}
