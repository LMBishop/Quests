package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class TaskTypeManager {

    private final Quests plugin;
    private boolean allowRegistrations;

    public TaskTypeManager(Quests plugin) {
        this.plugin = plugin;
        allowRegistrations = true;
    }

    public void closeRegistrations() {
        allowRegistrations = false;
    }

    public boolean areRegistrationsAccepted() {
        return allowRegistrations;
    }

    private ArrayList<TaskType> taskTypes = new ArrayList<>();

    public ArrayList<TaskType> getTaskTypes() {
        return taskTypes;
    }

    public void resetTaskTypes() {
        for (TaskType taskType : taskTypes) {
            taskType.getRegisteredQuests().clear();
        }
    }

    public void registerTaskType(TaskType taskType) {
        if (!allowRegistrations) {
            throw new IllegalStateException("No longer accepting new task types (must be done before quests are loaded)");
        }
        Bukkit.getPluginManager().registerEvents(taskType, plugin);
        plugin.getQuestsLogger().info("Task type " + taskType.getType() + " has been registered.");
        taskTypes.add(taskType);
    }

    public void registerQuestTasksWithTaskTypes(Quest quest) {
        if (allowRegistrations) {
            throw new IllegalStateException("Still accepting new task types (type registrations must be closed before registering quests)");
        }
        for (Task task : quest.getTasks()) {
            TaskType t;
            if ((t = getTaskType(task.getType())) != null) {
                t.registerQuest(quest);
            }
        }
    }

    public TaskType getTaskType(String string) {
        for (TaskType taskType : taskTypes) {
            if (taskType.getType().equalsIgnoreCase(string)) {
                return taskType;
            }
        }
        return null;
    }
}
