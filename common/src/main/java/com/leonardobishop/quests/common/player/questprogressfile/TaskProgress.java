package com.leonardobishop.quests.common.player.questprogressfile;

import java.util.UUID;

public class TaskProgress {

    private final String taskid;
    private final UUID player;

    private QuestProgress linkedQuestProgress;
    private boolean modified;
    private Object progress;
    private boolean completed;

    public TaskProgress(QuestProgress linkedQuestProgress, String taskid, Object progress, UUID player, boolean completed) {
        this.linkedQuestProgress = linkedQuestProgress;
        this.taskid = taskid;
        this.progress = progress;
        this.player = player;
        this.completed = completed;
    }

    public TaskProgress(QuestProgress linkedQuestProgress, String taskid, Object progress, UUID player, boolean completed, boolean modified) {
        this(linkedQuestProgress, taskid, progress, player, completed);
        this.modified = modified;
    }

    public TaskProgress(TaskProgress taskProgress) {
        this.taskid = taskProgress.taskid;
        this.player = taskProgress.player;
        this.modified = taskProgress.modified;
        this.progress = taskProgress.progress;
        this.completed = taskProgress.completed;
    }

    public String getTaskId() {
        return taskid;
    }

    public Object getProgress() {
        return progress;
    }

    public void setProgress(Object progress) {
        if (this.progress != progress) this.modified = true;

        this.progress = progress;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean complete) {
        // do not queue completion for already completed quests
        // https://github.com/LMBishop/Quests/issues/543
        if (this.completed == complete) {
            return;
        }

        this.completed = complete;
        this.modified = true;

        if (complete) {
            linkedQuestProgress.queueForCompletionTest();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void resetModified() {
        this.modified = false;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
