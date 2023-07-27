package com.leonardobishop.quests.bukkit.scheduler.folia;

import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FoliaWrappedTask implements WrappedTask {

    private final ScheduledTask scheduledTask;

    public FoliaWrappedTask(@NotNull ScheduledTask scheduledTask) {
        this.scheduledTask = Objects.requireNonNull(scheduledTask);
    }

    @Override
    public void cancel() {
        scheduledTask.cancel();
    }

    @Override
    public boolean isCancelled() {
        return scheduledTask.isCancelled();
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return scheduledTask.getOwningPlugin();
    }
}
