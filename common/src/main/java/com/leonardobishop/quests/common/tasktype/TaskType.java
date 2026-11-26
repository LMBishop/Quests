package com.leonardobishop.quests.common.tasktype;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a type of task that can be used within quests. A {@link Quest}
 * will be associated with this task type if it contains at least one task
 * of this type, allowing for efficient quest management without the need
 * to iterate through every single quest.
 * <p>
 * Each task type has its own {@link Task} implementation {@code T}, described by a {@link TaskSchema}.
 * The schema is used once per task while quests are loaded to parse and validate its options.
 *
 * @param <T> the task implementation of this task type
 */
@NullMarked
public abstract class TaskType<T extends Task> {

    protected final String type;
    private final @Nullable String author;
    private final @Nullable String description;
    private final TaskSchema<T> schema;
    private final List<String> aliases;
    private final List<Quest> quests;

    /**
     * Constructs a new TaskType with the specified parameters.
     *
     * @param type        the name of the task type; must not contain spaces
     * @param author      the name of the person (or people) who created this task type; can be null
     * @param description a short description of the task type; can be null
     * @param schema      the schema of tasks of this type; must not be null
     * @param aliases     an array of alternative names for this task type; must not be null
     */
    public TaskType(final String type, final @Nullable String author, final @Nullable String description, final TaskSchema<T> schema, final String... aliases) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(schema, "schema cannot be null");
        Objects.requireNonNull(aliases, "aliases cannot be null");

        this.type = type;
        this.author = author;
        this.description = description;
        this.schema = schema;
        this.aliases = List.of(aliases);
        this.quests = new ArrayList<>();
    }

    /**
     * Constructs a new TaskType with the specified type, author, and description.
     *
     * @param type        the name of the task type; must not contain spaces
     * @param author      the name of the person (or people) who created this task type; can be null
     * @param description a short description of the task type; can be null
     * @param schema      the schema of tasks of this type; must not be null
     */
    public TaskType(final String type, final @Nullable String author, final @Nullable String description, final TaskSchema<T> schema) {
        this(type, author, description, schema, new String[0]);
    }

    /**
     * Constructs a new TaskType with the specified type.
     *
     * @param type   the name of the task type; must not contain spaces
     * @param schema the schema of tasks of this type; must not be null
     */
    public TaskType(final String type, final TaskSchema<T> schema) {
        this(type, null, null, schema);
    }

    /**
     * Returns the name of this task type.
     *
     * @return the task type name
     */
    @Contract(pure = true)
    public final String getType() {
        return this.type;
    }

    /**
     * Returns the author of this task type.
     *
     * @return the author's name, or null if not specified
     */
    @Contract(pure = true)
    public final @Nullable String getAuthor() {
        return this.author;
    }

    /**
     * Returns the description of this task type.
     *
     * @return the description, or null if not specified
     */
    @Contract(pure = true)
    public final @Nullable String getDescription() {
        return this.description;
    }

    /**
     * Returns the schema describing the options accepted by tasks of this type.
     *
     * @return the task schema
     */
    @Contract(pure = true)
    public final TaskSchema<T> getSchema() {
        return this.schema;
    }

    /**
     * Returns an unmodifiable list of aliases for this task type.
     *
     * @return an unmodifiable list of aliases
     */
    @Contract(pure = true)
    public final @Unmodifiable List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Returns an unmodifiable list of all registered quests for this task type.
     *
     * @return an unmodifiable list of registered quests
     */
    @Contract(pure = true)
    public final @UnmodifiableView List<Quest> getRegisteredQuests() {
        return Collections.unmodifiableList(this.quests);
    }

    /**
     * Registers a quest to this task type. This is typically done when
     * all quests are initially loaded.
     *
     * @param quest the quest to register; must not be null
     */
    public final void registerQuest(final Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        if (!this.quests.contains(quest)) {
            this.quests.add(quest);
        }
    }

    /**
     * Clears all registered quests from this task type.
     */
    public final void unregisterAll() {
        this.quests.clear();
    }

    /**
     * Called when all quests have been registered to this task type.
     * This method may be called multiple times if an operator uses
     * the /quests admin reload command.
     */
    public void onReady() {
        // Not implemented here
    }

    /**
     * Called when a player starts a quest that contains a task of this type.
     *
     * @param quest      the quest being started
     * @param task       the task being started
     * @param playerUUID the UUID of the player starting the quest
     */
    public void onStart(final Quest quest, final T task, final UUID playerUUID) {
        // Not implemented here
    }

    /**
     * Called when this task type is disabled.
     */
    public void onDisable() {
        // Not implemented here
    }

    /**
     * Returns all tasks of this type in the given quest.
     *
     * @param quest the quest
     * @return tasks of this type
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public final List<T> getTasks(final Quest quest) {
        return (List<T>) quest.getTasksOfType(this.type);
    }

    /**
     * Calls {@link #onStart(Quest, Task, UUID)} for a task of unknown static type.
     *
     * @throws ClassCastException if the task was not created by this task type
     */
    @SuppressWarnings("unchecked")
    public final void start(final Quest quest, final Task task, final UUID playerUUID) {
        this.onStart(quest, (T) task, playerUUID);
    }
}
