package com.leonardobishop.quests.common.matcher;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Matches a single, possibly absent, piece of text (e.g. an item name or one lore line).
 * <p>
 * Configuration forms:
 * <pre>
 * name: "Sword"            # equals, case sensitive
 * name: ""                 # present and empty
 * name:
 *   mode: absent           # no value at all
 * name:
 *   mode: present          # any value
 * name:
 *   text: "sword"
 *   mode: contains         # equals | starts-with | contains | ends-with
 *   ignore-case: true
 * </pre>
 */
@NullMarked
public final class TextMatcher {

    private static final Set<String> KNOWN_KEYS = Set.of("text", "mode", "ignore-case");

    private final Mode mode;
    private final @Nullable String text;
    private final boolean ignoreCase;

    private TextMatcher(final Mode mode, final @Nullable String text, final boolean ignoreCase) {
        this.mode = mode;
        this.text = text;
        this.ignoreCase = ignoreCase;
    }

    public static TextMatcher of(final Mode mode, final @Nullable String text, final boolean ignoreCase) {
        Objects.requireNonNull(mode, "mode cannot be null");

        if (mode.requiresText() != (text != null)) {
            throw new IllegalArgumentException("mode " + mode + (mode.requiresText() ? " requires" : " does not accept") + " a text");
        }

        return new TextMatcher(mode, text, ignoreCase);
    }

    public static TextMatcher equalTo(final String text) {
        return of(Mode.EQUALS, text, false);
    }

    @Contract(pure = true)
    public Mode getMode() {
        return this.mode;
    }

    @Contract(pure = true)
    public @Nullable String getText() {
        return this.text;
    }

    @Contract(pure = true)
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    /**
     * @param value the value to test, or null if absent
     * @return whether the value satisfies this matcher
     */
    @Contract(pure = true)
    public boolean matches(final @Nullable String value) {
        return switch (this.mode) {
            case ABSENT -> value == null;
            case PRESENT -> value != null;
            case EQUALS -> value != null && (this.ignoreCase ? value.equalsIgnoreCase(this.text) : value.equals(this.text));
            case STARTS_WITH -> value != null && value.regionMatches(this.ignoreCase, 0, this.text, 0, this.text.length());
            case ENDS_WITH -> value != null && value.length() >= this.text.length()
                    && value.regionMatches(this.ignoreCase, value.length() - this.text.length(), this.text, 0, this.text.length());
            case CONTAINS -> value != null && contains(value, this.text, this.ignoreCase);
        };
    }

    /**
     * @return whether this matcher describes exactly one value (case sensitive equality or absence),
     * i.e. whether it can be turned into a concrete value
     */
    @Contract(pure = true)
    public boolean isExact() {
        return (this.mode == Mode.EQUALS && !this.ignoreCase) || this.mode == Mode.ABSENT;
    }

    private static boolean contains(final String value, final String text, final boolean ignoreCase) {
        if (!ignoreCase) {
            return value.contains(text);
        }

        final int max = value.length() - text.length();
        for (int i = 0; i <= max; i++) {
            if (value.regionMatches(true, i, text, 0, text.length())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parser accepting a scalar (equals) or a section with {@code text}, {@code mode} and {@code ignore-case}.
     *
     * @param textTransformer applied to configured texts (e.g. translating colour codes)
     */
    public static TaskKeyParser<TextMatcher> parser(final UnaryOperator<String> textTransformer) {
        Objects.requireNonNull(textTransformer, "textTransformer cannot be null");

        return (raw, context) -> {
            if (raw instanceof String || raw instanceof Number || raw instanceof Boolean) {
                return equalTo(textTransformer.apply(String.valueOf(raw)));
            }

            if (!(raw instanceof final Map<?, ?> map)) {
                context.error("Expected a text or a text matcher section for '" + context.getPath() + "', but got '" + raw + "' instead");
                return null;
            }

            for (final Object key : map.keySet()) {
                if (!KNOWN_KEYS.contains(String.valueOf(key))) {
                    context.warning("Unknown text matcher option '" + key + "' in '" + context.getPath() + "' (known: text, mode, ignore-case)");
                }
            }

            final Object rawMode = map.get("mode");
            final Mode mode;
            if (rawMode == null) {
                mode = Mode.EQUALS;
            } else {
                mode = Mode.fromConfigName(String.valueOf(rawMode));
                if (mode == null) {
                    context.error("Unknown text matcher mode '" + rawMode + "' in '" + context.getPath() + "' (accepted: " + Mode.configNames() + ")");
                    return null;
                }
            }

            final Object rawText = map.get("text");
            if (mode.requiresText() && rawText == null) {
                context.error("Text matcher mode '" + mode.getConfigName() + "' in '" + context.getPath() + "' requires a 'text'");
                return null;
            }
            if (!mode.requiresText() && rawText != null) {
                context.error("Text matcher mode '" + mode.getConfigName() + "' in '" + context.getPath() + "' cannot be used with a 'text'");
                return null;
            }
            if (rawText instanceof Map || rawText instanceof Iterable) {
                context.error("Expected 'text' in '" + context.getPath() + "' to be a text, but got '" + rawText + "' instead");
                return null;
            }

            final Object rawIgnoreCase = map.get("ignore-case");
            boolean ignoreCase = false;
            if (rawIgnoreCase != null) {
                if (!(rawIgnoreCase instanceof final Boolean b)) {
                    context.error("Expected 'ignore-case' in '" + context.getPath() + "' to be a boolean, but got '" + rawIgnoreCase + "' instead");
                    return null;
                }
                ignoreCase = b;
                if (!mode.requiresText()) {
                    context.warning("'ignore-case' has no effect with text matcher mode '" + mode.getConfigName() + "' in '" + context.getPath() + "'");
                }
            }

            return of(mode, rawText != null ? textTransformer.apply(String.valueOf(rawText)) : null, ignoreCase);
        };
    }

    @Override
    public String toString() {
        return switch (this.mode) {
            case ABSENT, PRESENT -> "<" + this.mode.getConfigName() + ">";
            default -> this.mode.getConfigName() + (this.ignoreCase ? " (ignore case) '" : " '") + this.text + "'";
        };
    }

    public enum Mode {
        EQUALS(true),
        STARTS_WITH(true),
        CONTAINS(true),
        ENDS_WITH(true),
        PRESENT(false),
        ABSENT(false);

        private static final Map<String, Mode> BY_CONFIG_NAME = new LinkedHashMap<>();

        static {
            for (final Mode mode : values()) {
                BY_CONFIG_NAME.put(mode.getConfigName(), mode);
            }
        }

        private final boolean requiresText;

        Mode(final boolean requiresText) {
            this.requiresText = requiresText;
        }

        public boolean requiresText() {
            return this.requiresText;
        }

        /**
         * @return name used in configuration, e.g. {@code starts-with}
         */
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

    /**
     * Reports that a matcher cannot be used where a concrete value is required.
     */
    public static void reportNotExact(final TaskKeyContext context, final String what) {
        context.report(ConfigProblem.ConfigProblemType.ERROR,
                what + " in '" + context.getPath() + "' must use mode 'equals' without 'ignore-case' (or 'absent') for item type 'defined-exact'",
                "'defined-exact' builds one concrete item and compares it<br>" +
                        "with the full item data, so every option must describe<br>" +
                        "exactly one value. Use type 'defined' to match partially.");
    }
}
