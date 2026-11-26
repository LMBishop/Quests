package com.leonardobishop.quests.common.tasktype.key;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Converts a raw configuration value into a typed value.
 * <p>
 * Parsers run exactly once per task, when quests are loaded. A parser that cannot produce a value
 * must report the reason through {@link TaskKeyContext} and return {@code null}; the option is then
 * treated as not configured. A parser may also report a warning and still return a value.
 *
 * @param <T> type of the parsed value
 */
@NullMarked
@FunctionalInterface
public interface TaskKeyParser<T> {

    /**
     * @param raw     the raw (non-null) value read from the configuration
     * @param context the parsing context used to report problems
     * @return the parsed value, or null if the value is invalid
     */
    @Nullable T parse(Object raw, TaskKeyContext context);

    /**
     * @param mapper function applied to every successfully parsed value
     * @param <R>    new value type
     * @return a parser which maps the result of this parser
     */
    default <R> TaskKeyParser<R> map(final Function<? super T, ? extends @Nullable R> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        return (raw, context) -> {
            final T value = this.parse(raw, context);
            return value != null ? mapper.apply(value) : null;
        };
    }
}
