package com.leonardobishop.quests.player.questprogressfile;

import java.util.UUID;

public class TaskProgress {

    private boolean modified;
    private String taskid;
    private Object progress;
    private UUID player;
    private boolean completed;

    public TaskProgress(String taskid, Object progress, UUID player, boolean completed) {
        this.taskid = taskid;
        this.progress = progress;
        this.completed = completed;
        this.player = player;
    }

    public TaskProgress(String taskid, Object progress, UUID player, boolean completed, boolean modified) {
        this(taskid, progress, player, completed);
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
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
