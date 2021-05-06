package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.quests.Quest;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A task type which can be used within Quests. A {@link Quest}
 * will be registered to this if it contains at least 1 task
 * which is of this type. This is so you do not have to
 * iterate through every single quest.
 */
public abstract class TaskType implements Listener {

    private final List<Quest> quests = new ArrayList<>();
    private final String type;
    private String author;
    private String description;

    /**
     * @param type the name of the task type, should not contain spaces
     * @param author the name of the person (or people) who wrote it
     * @param description a short, simple description of the task type
     */
    public TaskType(String type, String author, String description) {
        this.type = type;
        this.author = author;
        this.description = description;
    }

    /**
     * @param type the name of the task type, should not contain spaces
     */
    public TaskType(String type) {
        this.type = type;
    }

    /**
     * Registers a {@link Quest} to this task type. This is usually done when
     * all the quests are initially loaded.
     *
     * @param quest the {@link Quest} to register.
     */
    public final void registerQuest(Quest quest) {
        if (!quests.contains(quest)) {
            quests.add(quest);
        }
    }

    /**
     * Clears the list which contains the registered quests.
     */
    public final void unregisterAll() {
        quests.clear();
    }

    /**
     * @return {@link List} of type {@link Quest} of all registered quests.
     */
    public final List<Quest> getRegisteredQuests() {
        return quests;
    }

    public final String getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public List<ConfigValue> getCreatorConfigValues() {
        return Collections.emptyList();
    }

    public void onReady() {
        // not implemented here
    }

    public void onDisable() {
        // not implemented here
    }
}
