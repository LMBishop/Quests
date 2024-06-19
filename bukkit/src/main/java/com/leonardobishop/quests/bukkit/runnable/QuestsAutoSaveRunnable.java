package com.leonardobishop.quests.bukkit.runnable;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import com.leonardobishop.quests.bukkit.scheduler.WrappedTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class QuestsAutoSaveRunnable extends WrappedRunnable {

    private final BukkitQuestsPlugin plugin;
    private final Deque<UUID> saveDeque;
    private int lastSaveSize;
    private WrappedTask task;

    public QuestsAutoSaveRunnable(final @NotNull BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.saveDeque = new ArrayDeque<>();
        this.lastSaveSize = 0;
    }

    @Override
    public void run() {
        // log it just to be sure
        QuestsAutoSaveRunnable.this.plugin.getQuestsLogger().debug("Ran outer autosave runnable");

        // do not call force save if there were no players
        if (this.lastSaveSize != 0) {
            // we need to force save for remaining elements in case
            // user set the interval option to a way too low value
            this.forceSave(false);
        }

        // queue all the current online players
        // it updates lastSaveSize field
        this.updateSaveDeque();

        // do not schedule the timer if there is no players
        if (this.lastSaveSize == 0) {
            return;
        }

        // log it just to be sure
        QuestsAutoSaveRunnable.this.plugin.getQuestsLogger().debug("Scheduled inner autosave runnable");

        // run autosave task timer again
        this.task = new WrappedRunnable() {
            @Override
            public void run() {
                final UUID playerId = QuestsAutoSaveRunnable.this.saveDeque.poll();

                // cancel the task as there is nothing to save
                if (playerId == null) {
                    this.cancel();
                    return;
                }

                // don't do that for offline as they will be saved on quit
                final Player player = QuestsAutoSaveRunnable.this.plugin.getServer().getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    QuestsAutoSaveRunnable.this.save(playerId);
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                QuestsAutoSaveRunnable.this.plugin.getQuestsLogger().debug("Cancelled inner autosave runnable");
                super.cancel();
            }
        }.runTaskTimer(this.plugin.getScheduler(), 2L, 2L);
    }

    private void forceSave(final boolean cancel) {
        this.plugin.getQuestsLogger().debug("Called forceSave with cancel: " + cancel);

        // cancel the task so it can be rescheduled
        if (this.task != null && !this.task.isCancelled()) {
            this.task.cancel();
        }

        // log about needed config change
        final int size = this.saveDeque.size();
        if (!cancel && size > 0) {
            this.plugin.getLogger().warning("Forced autosave for " + size + " of " + this.lastSaveSize + " players scheduled. "
                    + "To avoid forced saves in the future and maintain smooth server performance, please consider increasing the "
                    + "options.performance-tweaking.quest-autosave-interval setting.");
        }

        // force save leftovers
        for (int i = 0; i < size; i++) {
            final UUID uniqueId = this.saveDeque.removeFirst();

            // joining is evil, but we can assume now that all was saved properly
            // which makes us able to just continue and schedule another autosave
            this.save(uniqueId).join();
        }
    }

    private @NotNull CompletableFuture<Void> save(final @NotNull UUID uniqueId) {
        if (this.plugin.getServer().getPlayer(uniqueId) != null) {
            return this.plugin.getPlayerManager().savePlayer(uniqueId);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private void updateSaveDeque() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.saveDeque.add(player.getUniqueId());
        }
        this.lastSaveSize = this.saveDeque.size();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.forceSave(true); // we need to force autosave

        this.plugin.getQuestsLogger().debug("Cancelled outer autosave runnable");
        super.cancel();
    }
}
