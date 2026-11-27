package com.leonardobishop.quests.common.tasktype.key;

import com.leonardobishop.quests.common.config.ConfigProblem;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Context passed to a {@link TaskKeyParser} while a single option is being parsed.
 */
@NullMarked
public interface TaskKeyContext {

    /**
     * @return the resolved name of the task type being loaded
     */
    String getTaskType();

    /**
     * @return the path the value was actually read from (primary path or one of the aliases)
     */
    String getPath();

    /**
     * Reads another raw value from the same task section. Useful for options whose meaning
     * depends on a sibling option (e.g. legacy {@code data} values).
     *
     * @param path path inside the task section
     * @return the raw value, or null if absent
     */
    @Nullable Object getSibling(String path);

    /**
     * Reports a problem located at {@link #getPath()}.
     */
    void report(ConfigProblem.ConfigProblemType type, String description, @Nullable String extendedDescription);

    /**
     * Reports a problem located at {@code getPath() + "." + subPath}.
     */
    void report(ConfigProblem.ConfigProblemType type, String description, @Nullable String extendedDescription, String subPath);

    default void error(final String description) {
        this.report(ConfigProblem.ConfigProblemType.ERROR, description, null);
    }

    default void warning(final String description) {
        this.report(ConfigProblem.ConfigProblemType.WARNING, description, null);
    }

    /**
     * Creates a context for a nested value, e.g. one option inside a section. Its {@link #getPath()}
     * is {@code getPath() + "." + subPath} and every problem is reported at that location.
     *
     * @param subPath path of the nested value relative to {@link #getPath()}
     * @return a child context delegating to this one
     */
    default TaskKeyContext child(final String subPath) {
        final TaskKeyContext parent = this;

        return new TaskKeyContext() {
            @Override
            public String getTaskType() {
                return parent.getTaskType();
            }

            @Override
            public String getPath() {
                return parent.getPath() + "." + subPath;
            }

            @Override
            public @Nullable Object getSibling(final String path) {
                return parent.getSibling(path);
            }

            @Override
            public void report(final ConfigProblem.ConfigProblemType type, final String description, final @Nullable String extendedDescription) {
                parent.report(type, description, extendedDescription, subPath);
            }

            @Override
            public void report(final ConfigProblem.ConfigProblemType type, final String description, final @Nullable String extendedDescription, final String nestedPath) {
                parent.report(type, description, extendedDescription, subPath + "." + nestedPath);
            }
        };
    }
}
