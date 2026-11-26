package com.leonardobishop.quests.common.tasktype.key;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.quest.Task;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Describes a {@link Task} implementation: which {@link TaskKey keys} it accepts, which cross-key
 * rules apply and how an instance is created from parsed {@link TaskOptions}.
 * <p>
 * {@link #load(String, String, Map, List)} is called once per task when quests are loaded. Every
 * raw value is parsed exactly once there; the resulting task only exposes typed values.
 *
 * @param <T> task implementation
 */
@NullMarked
public final class TaskSchema<T extends Task> {

    private final Factory<T> factory;
    private final Set<TaskKey<?>> keys;
    private final List<Validator> validators;
    private final Set<String> paths;

    private TaskSchema(final Factory<T> factory, final Set<TaskKey<?>> keys, final List<Validator> validators) {
        this.factory = factory;
        this.keys = Collections.unmodifiableSet(new LinkedHashSet<>(keys));
        this.validators = List.copyOf(validators);

        final Set<String> paths = new LinkedHashSet<>();

        for (final TaskKey<?> key : keys) {
            paths.add(key.getPath());
            paths.addAll(key.getAliases());
        }

        this.paths = Collections.unmodifiableSet(paths);
    }

    public static <T extends Task> Builder<T> builder(final Factory<T> factory) {
        return new Builder<>(factory);
    }

    /**
     * @return all registered keys in registration order
     */
    @Contract(pure = true)
    public @Unmodifiable Set<TaskKey<?>> getKeys() {
        return this.keys;
    }

    /**
     * @return every path (primary paths and aliases) accepted by this schema, in registration order
     */
    @Contract(pure = true)
    public @Unmodifiable Set<String> getPaths() {
        return this.paths;
    }

    /**
     * @param path a path inside the task section
     * @return whether one of the registered keys reads this path
     */
    @Contract(pure = true)
    public boolean acceptsPath(final String path) {
        return this.paths.contains(path);
    }

    /**
     * Parses the raw task section and creates the task.
     *
     * @param id       task id
     * @param type     resolved task type name
     * @param raw      raw values of the task section (already merged with the global task configuration)
     * @param problems list receiving problems; locations are relative to the task section
     * @return the created task, or null if an error prevents it from being loaded
     */
    public @Nullable T load(final String id, final String type, final Map<String, ?> raw, final List<ConfigProblem> problems) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(raw, "raw cannot be null");
        Objects.requireNonNull(problems, "problems cannot be null");

        final List<ConfigProblem> taskProblems = new ArrayList<>();
        final Map<TaskKey<?>, Object> values = new LinkedHashMap<>();

        for (final TaskKey<?> key : this.keys) {
            final String path = findPath(key, raw);

            if (path == null) {
                if (key.isRequired()) {
                    taskProblems.add(new ConfigProblem(
                            ConfigProblem.ConfigProblemType.ERROR,
                            ConfigProblemDescriptions.TASK_MISSING_FIELD.getDescription(key.getPath(), type),
                            ConfigProblemDescriptions.TASK_MISSING_FIELD.getExtendedDescription(key.getPath(), type),
                            key.getPath()
                    ));
                }

                continue;
            }

            final Context context = new Context(type, path, raw, taskProblems);
            final Object value = key.getParser().parse(raw.get(path), context);

            if (value != null) {
                values.put(key, value);
            } else if (key.isRequired() && !context.reportedError) {
                taskProblems.add(new ConfigProblem(
                        ConfigProblem.ConfigProblemType.ERROR,
                        "Expected a valid value for '" + path + "'",
                        null,
                        path
                ));
            }
        }

        final TaskOptions options = new TaskOptions(this.keys, values);

        if (!hasError(taskProblems)) {
            for (final Validator validator : this.validators) {
                validator.validate(options, taskProblems);
            }
        }

        T task = null;

        if (!hasError(taskProblems)) {
            try {
                task = this.factory.create(id, type, options);
            } catch (final RuntimeException e) {
                taskProblems.add(new ConfigProblem(
                        ConfigProblem.ConfigProblemType.ERROR,
                        "Task could not be created: " + e,
                        null,
                        ""
                ));
            }
        }

        problems.addAll(taskProblems);

        return task;
    }

    private static @Nullable String findPath(final TaskKey<?> key, final Map<String, ?> raw) {
        if (raw.get(key.getPath()) != null) {
            return key.getPath();
        }

        for (final String alias : key.getAliases()) {
            if (raw.get(alias) != null) {
                return alias;
            }
        }

        return null;
    }

    private static boolean hasError(final List<ConfigProblem> problems) {
        for (final ConfigProblem problem : problems) {
            if (problem.getType() == ConfigProblem.ConfigProblemType.ERROR) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a task from parsed options. Usually a constructor reference.
     */
    @FunctionalInterface
    public interface Factory<T extends Task> {

        T create(String id, String type, TaskOptions options);
    }

    /**
     * A rule spanning more than one key. Runs only if parsing every key produced no errors.
     */
    @FunctionalInterface
    public interface Validator {

        /**
         * @param options  parsed options
         * @param problems list receiving problems; locations are relative to the task section
         */
        void validate(TaskOptions options, List<ConfigProblem> problems);
    }

    public static final class Builder<T extends Task> {

        private final Factory<T> factory;
        private final Set<TaskKey<?>> keys;
        private final List<Validator> validators;

        private Builder(final Factory<T> factory) {
            this.factory = Objects.requireNonNull(factory, "factory cannot be null");
            this.keys = new LinkedHashSet<>();
            this.validators = new ArrayList<>();
        }

        public Builder<T> key(final TaskKey<?> key) {
            Objects.requireNonNull(key, "key cannot be null");

            for (final TaskKey<?> existing : this.keys) {
                if (existing != key && existing.getPath().equals(key.getPath())) {
                    throw new IllegalArgumentException("Another key is already registered for path '" + key.getPath() + "'");
                }
            }

            this.keys.add(key);
            return this;
        }

        public Builder<T> keys(final TaskKey<?>... keys) {
            Objects.requireNonNull(keys, "keys cannot be null");

            for (final TaskKey<?> key : keys) {
                this.key(key);
            }

            return this;
        }

        public Builder<T> keys(final Collection<? extends TaskKey<?>> keys) {
            Objects.requireNonNull(keys, "keys cannot be null");

            for (final TaskKey<?> key : keys) {
                this.key(key);
            }

            return this;
        }

        public Builder<T> validator(final Validator validator) {
            Objects.requireNonNull(validator, "validator cannot be null");

            this.validators.add(validator);
            return this;
        }

        public TaskSchema<T> build() {
            return new TaskSchema<>(this.factory, this.keys, this.validators);
        }
    }

    private static final class Context implements TaskKeyContext {

        private final String taskType;
        private final String path;
        private final Map<String, ?> raw;
        private final List<ConfigProblem> problems;
        private boolean reportedError;

        private Context(final String taskType, final String path, final Map<String, ?> raw, final List<ConfigProblem> problems) {
            this.taskType = taskType;
            this.path = path;
            this.raw = raw;
            this.problems = problems;
        }

        @Override
        public String getTaskType() {
            return this.taskType;
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public @Nullable Object getSibling(final String path) {
            return this.raw.get(path);
        }

        @Override
        public void report(final ConfigProblem.ConfigProblemType type, final String description, final @Nullable String extendedDescription) {
            this.add(type, description, extendedDescription, this.path);
        }

        @Override
        public void report(final ConfigProblem.ConfigProblemType type, final String description, final @Nullable String extendedDescription, final String subPath) {
            this.add(type, description, extendedDescription, this.path + "." + subPath);
        }

        private void add(final ConfigProblem.ConfigProblemType type, final String description, final @Nullable String extendedDescription, final String location) {
            if (type == ConfigProblem.ConfigProblemType.ERROR) {
                this.reportedError = true;
            }

            this.problems.add(new ConfigProblem(type, description, extendedDescription, location));
        }
    }
}
