package com.leonardobishop.quests.player.questprogressfile;

import java.util.UUID;

public class TaskProgress {

    private final QuestProgress questProgress;
    private final String taskid;
    private final UUID player;

    private boolean modified;
    private Object progress;
    private boolean completed;

    public TaskProgress(QuestProgress questProgress, String taskid, Object progress, UUID player, boolean completed) {
        this.questProgress = questProgress;
        this.taskid = taskid;
        this.progress = progress;
        this.player = player;
        this.completed = completed;
    }

    public TaskProgress(QuestProgress questProgress, String taskid, Object progress, UUID player, boolean completed, boolean modified) {
        this(questProgress, taskid, progress, player, completed);
        this.modified = modified;
    }

    public String getTaskId() {
        return taskid;
    }

    public Object getProgress() {
        return progress;
    }

    public void setProgress(Object progress) {
        this.progress = progress;
        this.modified = true;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean complete) {
        this.completed = complete;
        this.modified = true;

        if (complete) {
            questProgress.queueForCompletionTest();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
