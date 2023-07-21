package com.leonardobishop.quests.bukkit.scheduler.folia;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.ServerScheduler;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FoliaServerScheduler implements ServerScheduler {

    public static final boolean FOLIA;
    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;
    }

    private final BukkitQuestsPlugin plugin;

    private final GlobalRegionScheduler globalRegionScheduler;
    private final AsyncScheduler asyncScheduler;
    private final RegionScheduler regionScheduler;

    public FoliaServerScheduler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;

        final Server server = plugin.getServer();
        this.globalRegionScheduler = server.getGlobalRegionScheduler();
        this.asyncScheduler = server.getAsyncScheduler();
        this.regionScheduler = server.getRegionScheduler();
    }

    @Override
    public void cancelAllTasks() {
        globalRegionScheduler.cancelTasks(plugin);
        asyncScheduler.cancelTasks(plugin);
    }

    @Override
    public void cancelTask(@NotNull WrappedTask wrappedTask) {
        wrappedTask.cancel();
    }

    @Override
    public @NotNull WrappedTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        return new FoliaWrappedTask(globalRegionScheduler.runAtFixedRate(plugin, task -> {
            if (runnable != null) runnable.run();
        }, delay, period));
    }

    @Override
    public @NotNull WrappedTask runTaskTimerAsynchronously(@NotNull Runnable runnable, long delay, long period) {
        return new FoliaWrappedTask(asyncScheduler.runAtFixedRate(plugin, task -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public @NotNull WrappedTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return new FoliaWrappedTask(globalRegionScheduler.runDelayed(plugin, task -> runnable.run(), delay));
    }

    @Override
    public @NotNull WrappedTask runTaskLaterAsynchronously(@NotNull Runnable runnable, long delay) {
        return new FoliaWrappedTask(asyncScheduler.runDelayed(plugin, task -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public @NotNull WrappedTask runTaskLaterAtEntity(@NotNull Entity entity, @NotNull Runnable runnable, long delay) {
        return new FoliaWrappedTask(Objects.requireNonNull(entity.getScheduler().runDelayed(plugin, task -> runnable.run(), () -> {}, delay)));
    }

    @Override
    public void doSync(Runnable runnable) {
        globalRegionScheduler.run(plugin, task -> runnable.run());
    }

    @Override
    public void doAsync(Runnable runnable) {
        asyncScheduler.runNow(plugin, task -> runnable.run());
    }
}
