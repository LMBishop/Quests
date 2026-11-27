package com.leonardobishop.quests.common.matcher;

import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Matches the lines of an item lore against a set of <b>line conditions</b> ({@link TextMatcher}s), each
 * describing one lore line.
 * <p>
 * Modes:
 * <ul>
 *     <li>{@code exact} (default): the lore has as many lines as there are conditions, and lore line {@code n}
 *     satisfies condition {@code n}</li>
 *     <li>{@code ordered}: every condition is satisfied by a different lore line, in the order of the conditions;
 *     other lore lines may appear before, between and after them</li>
 *     <li>{@code contains-all}: every condition is satisfied by a different lore line, in any order; other lore
 *     lines may appear</li>
 *     <li>{@code contains-any}: at least one lore line satisfies at least one condition</li>
 *     <li>{@code none}: no lore line satisfies any condition</li>
 * </ul>
 * A set without conditions ({@code []}) means "no lore", whatever the mode.
 * Configuration forms (see {@link #listParser(UnaryOperator)}):
 * <pre>
 * lore:                    # one lore, exact
 *   - "Line 1"
 *   - text: "legendary"
 *     mode: contains
 * lore:                    # one lore with a mode
 *   mode: ordered
 *   lines: ["Line 1"]
 * lore:                    # several sets, any of them may match
 *   - ["A", "B"]
 *   - mode: contains-all
 *     lines: ["C"]
 * lore: []                 # no lore at all
 * </pre>
 */
@NullMarked
public final class LoreMatcher {

    private static final Set<String> KNOWN_KEYS = Set.of("mode", "lines");

    private final Mode mode;
    private final List<TextMatcher> lines;

    private LoreMatcher(final Mode mode, final List<TextMatcher> lines) {
        this.mode = mode;
        this.lines = List.copyOf(lines);
    }

    public static LoreMatcher of(final Mode mode, final List<TextMatcher> lines) {
        Objects.requireNonNull(mode, "mode cannot be null");
        Objects.requireNonNull(lines, "lines cannot be null");

        return new LoreMatcher(mode, lines);
    }

    @Contract(pure = true)
    public Mode getMode() {
        return this.mode;
    }

    @Contract(pure = true)
    public @Unmodifiable List<TextMatcher> getLines() {
        return this.lines;
    }

    /**
     * @param lore lines to test; null is treated as no lines
     */
    @Contract(pure = true)
    public boolean matches(final @Nullable List<String> lore) {
        final List<String> actual = lore != null ? lore : List.of();

        if (this.lines.isEmpty()) {
            return actual.isEmpty();
        }

        return switch (this.mode) {
            case EXACT -> this.matchesExact(actual);
            case ORDERED -> this.matchesOrdered(actual);
            case CONTAINS_ALL -> this.matchesContainsAll(actual);
            case CONTAINS_ANY -> this.matchesContainsAny(actual);
            case NONE -> !this.matchesContainsAny(actual);
        };
    }

    /**
     * @return whether this matcher describes exactly one lore, i.e. it can be turned into concrete lines
     */
    @Contract(pure = true)
    public boolean isExact() {
        if (this.mode != Mode.EXACT) {
            return false;
        }

        for (final TextMatcher line : this.lines) {
            if (line.getMode() != TextMatcher.Mode.EQUALS || line.isIgnoreCase()) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesExact(final List<String> actual) {
        if (actual.size() != this.lines.size()) {
            return false;
        }

        for (int i = 0; i < actual.size(); i++) {
            if (!this.lines.get(i).matches(actual.get(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesOrdered(final List<String> actual) {
        // greedy subsequence matching is optimal: matching a matcher as early as possible never hurts later ones
        int line = 0;
        for (final TextMatcher matcher : this.lines) {
            while (line < actual.size() && !matcher.matches(actual.get(line))) {
                line++;
            }
            if (line == actual.size()) {
                return false;
            }
            line++;
        }

        return true;
    }

    private boolean matchesContainsAny(final List<String> actual) {
        for (final String line : actual) {
            for (final TextMatcher condition : this.lines) {
                if (condition.matches(line)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesContainsAll(final List<String> actual) {
        if (this.lines.size() > actual.size()) {
            return false;
        }

        // bipartite matching (matcher -> distinct line) using augmenting paths
        final int[] lineOwner = new int[actual.size()];
        Arrays.fill(lineOwner, -1);

        for (int matcher = 0; matcher < this.lines.size(); matcher++) {
            if (!this.assign(matcher, actual, lineOwner, new boolean[actual.size()])) {
                return false;
            }
        }

        return true;
    }

    private boolean assign(final int matcher, final List<String> actual, final int[] lineOwner, final boolean[] visited) {
        for (int line = 0; line < actual.size(); line++) {
            if (visited[line] || !this.lines.get(matcher).matches(actual.get(line))) {
                continue;
            }

            visited[line] = true;
            if (lineOwner[line] == -1 || this.assign(lineOwner[line], actual, lineOwner, visited)) {
                lineOwner[line] = matcher;
                return true;
            }
        }

        return false;
    }

    /**
     * Parser for one or several lores; the result matches if any of the lores matches.
     *
     * @param textTransformer applied to configured texts (e.g. translating colour codes)
     */
    public static TaskKeyParser<List<LoreMatcher>> listParser(final UnaryOperator<String> textTransformer) {
        final TaskKeyParser<TextMatcher> lineParser = TextMatcher.parser(textTransformer);

        return (raw, context) -> {
            if (raw instanceof final Map<?, ?> map && map.containsKey("lines")) {
                final LoreMatcher lore = parseSection(map, lineParser, context);
                return lore != null ? List.of(lore) : null;
            }

            if (!(raw instanceof final List<?> list)) {
                // a single line
                final TextMatcher line = lineParser.parse(raw, context);
                return line != null ? List.of(of(Mode.EXACT, List.of(line))) : null;
            }

            boolean nested = false;
            boolean flat = false;
            for (final Object element : list) {
                if (element instanceof List || (element instanceof final Map<?, ?> m && m.containsKey("lines"))) {
                    nested = true;
                } else {
                    flat = true;
                }
            }

            if (nested && flat) {
                context.error("'" + context.getPath() + "' mixes lines and lores; use either a list of lines (one lore) or a list of lores");
                return null;
            }

            if (!nested) {
                final List<TextMatcher> lines = parseLines(list, lineParser, context);
                return lines != null ? List.of(of(Mode.EXACT, lines)) : null;
            }

            final List<LoreMatcher> lores = new ArrayList<>(list.size());
            for (final Object element : list) {
                final LoreMatcher lore = element instanceof final Map<?, ?> m
                        ? parseSection(m, lineParser, context)
                        : parseLinesAsLore((List<?>) element, lineParser, context);
                if (lore == null) {
                    return null;
                }
                lores.add(lore);
            }

            return Collections.unmodifiableList(lores);
        };
    }

    private static @Nullable LoreMatcher parseSection(final Map<?, ?> map, final TaskKeyParser<TextMatcher> lineParser, final TaskKeyContext context) {
        for (final Object key : map.keySet()) {
            if (!KNOWN_KEYS.contains(String.valueOf(key))) {
                context.warning("Unknown lore option '" + key + "' in '" + context.getPath() + "' (known: mode, lines)");
            }
        }

        final Object rawMode = map.get("mode");
        final Mode mode = rawMode != null ? Mode.fromConfigName(String.valueOf(rawMode)) : Mode.EXACT;
        if (mode == null) {
            context.error("Unknown lore mode '" + rawMode + "' in '" + context.getPath() + "' (accepted: " + Mode.configNames() + ")");
            return null;
        }

        final Object rawLines = map.get("lines");
        final List<?> lineList = rawLines instanceof final List<?> l ? l : List.of(rawLines);
        final List<TextMatcher> lines = parseLines(lineList, lineParser, context);
        return lines != null ? of(mode, lines) : null;
    }

    private static @Nullable LoreMatcher parseLinesAsLore(final List<?> list, final TaskKeyParser<TextMatcher> lineParser, final TaskKeyContext context) {
        final List<TextMatcher> lines = parseLines(list, lineParser, context);
        return lines != null ? of(Mode.EXACT, lines) : null;
    }

    private static @Nullable List<TextMatcher> parseLines(final List<?> list, final TaskKeyParser<TextMatcher> lineParser, final TaskKeyContext context) {
        final List<TextMatcher> lines = new ArrayList<>(list.size());
        for (final Object element : list) {
            if (element == null) {
                context.error("Lore line in '" + context.getPath() + "' cannot be null");
                return null;
            }
            final TextMatcher line = lineParser.parse(element, context);
            if (line == null) {
                // a lore with a skipped line would silently mean something else, so fail
                return null;
            }
            lines.add(line);
        }
        return lines;
    }

    @Override
    public String toString() {
        return this.mode.getConfigName() + " " + this.lines;
    }

    public enum Mode {
        EXACT,
        ORDERED,
        CONTAINS_ALL,
        CONTAINS_ANY,
        NONE;

        private static final Map<String, Mode> BY_CONFIG_NAME = new LinkedHashMap<>();

        static {
            for (final Mode mode : values()) {
                BY_CONFIG_NAME.put(mode.getConfigName(), mode);
            }
        }

        public String getConfigName() {
            return this.name().toLowerCase(Locale.ROOT).replace('_', '-');
        }

        public static @Nullable Mode fromConfigName(final String name) {
            return BY_CONFIG_NAME.get(name);
        }

        public static String configNames() {
            return String.join(", ", BY_CONFIG_NAME.keySet());
        }
    }
}
