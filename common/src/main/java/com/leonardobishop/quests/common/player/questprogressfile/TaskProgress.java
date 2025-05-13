package com.leonardobishop.quests.common.player.questprogressfile;

import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class TaskProgress {

    private final @Nullable QuestProgress questProgress;
    private final String taskId;
    private final UUID playerUUID;

    private @Nullable Object progress;
    private boolean completed;
    private boolean modified;

    /**
     * Constructs a TaskProgress.
     *
     * @param questProgress the quest progress
     * @param taskId        the associated task ID
     * @param playerUUID    the associated player UUID
     * @param progress      the progress object
     * @param completed     whether the task is completed
     * @param modified      whether the object has been modified and needs to be saved
     */
    public TaskProgress(final @Nullable QuestProgress questProgress, final String taskId, final UUID playerUUID, final @Nullable Object progress, final boolean completed, final boolean modified) {
        this.questProgress = questProgress;
        this.taskId = taskId;
        this.playerUUID = playerUUID;
        this.progress = progress;
        this.completed = completed;
        this.modified = modified;
    }

    /**
     * Constructs a TaskProgress with {@link TaskProgress#modified} set to {@code false}.
     *
     * @param questProgress the quest progress
     * @param taskId        the associated task ID
     * @param playerUUID    the associated player UUID
     * @param progress      the progress object
     * @param completed     whether the task is completed
     */
    public TaskProgress(final QuestProgress questProgress, final String taskId, final UUID playerUUID, final @Nullable Object progress, final boolean completed) {
        this(questProgress, taskId, playerUUID, progress, completed, false);
    }

    /**
     * Constructs a data-only clone from a TaskProgress instance.
     *
     * @param taskProgress the task progress instance
     */
    public TaskProgress(final TaskProgress taskProgress) {
        this(null, taskProgress.taskId, taskProgress.playerUUID, taskProgress.progress, taskProgress.completed, taskProgress.modified);
    }

    /**
     * @return the associated task ID
     */
    @Contract(pure = true)
    public String getTaskId() {
        return this.taskId;
    }

    /**
     * @return the associated player ID
     */
    @Contract(pure = true)
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * @return the progress object
     */
    @Contract(pure = true)
    public @Nullable Object getProgress() {
        return this.progress;
    }

    /**
     * @param progress the progress object
     */
    public void setProgress(final @Nullable Object progress) {
        if (Objects.equals(progress, this.progress)) {
            return;
        }

        this.progress = progress;
        this.modified = true;
    }

    /**
     * @return whether the task is completed
     */
    @Contract(pure = true)
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * @param completed whether the task is completed
     */
    public void setCompleted(final boolean completed) {
        if (this.questProgress == null) {
            throw new UnsupportedOperationException("associated quest progress cannot be null");
        }

        // do not queue completion for already completed quests
        // https://github.com/LMBishop/Quests/issues/543
        if (this.completed == completed) {
            return;
        }

        this.completed = completed;
        this.modified = true;

        if (completed) {
            this.questProgress.queueForCompletionTest();
        }
    }

    /**
     * @return whether the object has been modified and needs to be saved
     */
    @Contract(pure = true)
    public boolean isModified() {
        return this.modified;
    }

    /**
     * @param modified whether the object has been modified and needs to be saved
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    // DEPRECATED AND FOR REMOVAL

    /**
     * @return the associated player ID
     * @see QuestProgress#getPlayerUUID()
     */
    @Deprecated(forRemoval = true)
    @Contract(pure = true)
    public UUID getPlayer() {
        return this.playerUUID;
    }

    /**
     * It's equivalent to {@code TaskProgress#setModified(false)}.
     *
     * @see TaskProgress#setModified(boolean)
     */
    @Deprecated(forRemoval = true)
    public void resetModified() {
        this.setModified(false);
    }
}
