package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A task type which can be used within Quests. A {@link Quest}
 * will be registered to this if it contains at least 1 task
 * which is of this type. This is so you do not have to
 * iterate through every single quest.
 */
public abstract class TaskType {

    private final List<Quest> quests = new ArrayList<>();
    private final String type;
    private String author;
    private String description;

    /**
     * @param type the name of the task type, should not contain spaces
     * @param author the name of the person (or people) who wrote it
     * @param description a short, simple description of the task type
     */
    public TaskType(@NotNull String type, String author, String description) {
        this(type);
        this.author = author;
        this.description = description;
    }

    /**
     * @param type the name of the task type, should not contain spaces
     */
    public TaskType(@NotNull String type) {
        Objects.requireNonNull(type, "type cannot be null");

        this.type = type;
    }

    /**
     * Registers a {@link Quest} to this task type. This is usually done when
     * all the quests are initially loaded.
     *
     * @param quest the {@link Quest} to register.
     */
    public final void registerQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        if (!quests.contains(quest)) {
            quests.add(quest);
        }
    }

    /**
     * Clears the list which contains the registered quests.
     */
    protected final void unregisterAll() {
        quests.clear();
    }

    /**
     * @return immutable {@link List} of type {@link Quest} of all registered quests.
     */
    public final @NotNull List<Quest> getRegisteredQuests() {
        return Collections.unmodifiableList(quests);
    }

    public final @NotNull String getType() {
        return type;
    }

    public @Nullable String getAuthor() {
        return author;
    }

    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Called when Quests has finished registering all quests to the task type.
     * May be called several times if an operator uses /quests admin reload.
     */
    public void onReady() {
        // not implemented here
    }

    /**
     * Called when a player starts a quest containing a task of this type.
     */
    public void onStart(Quest quest, Task task, UUID playerUUID) {
        // not implemented here
    }

    public void onDisable() {
        // not implemented here
    }

    /**
     * Called when Quests reloads the configuration - used to detect errors in the configuration of your task type.
     *
     * @param root the root path for the config
     * @param config the config itself
     */
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        // not implemented here
        return Collections.emptyList();
    }
}
