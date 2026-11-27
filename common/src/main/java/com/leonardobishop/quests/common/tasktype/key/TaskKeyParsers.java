package com.leonardobishop.quests.common.tasktype.key;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.LongFunction;

/**
 * Platform independent {@link TaskKeyParser} implementations.
 * <p>
 * Every primitive type, {@link String} and enums have a {@code xValue()} parser for a single value and
 * a {@code xList()} parser accepting either a single value or a list of values. Numeric and character
 * parsers have overloads taking an inclusive {@code [min, max]} range; the overloads without a range
 * accept the whole range of the type. Any parser can be turned into a list or set parser with
 * {@link #list(TaskKeyParser)} and {@link #set(TaskKeyParser)}.
 * <p>
 * Parsers are strict about the YAML type: integral parsers do not accept decimals or strings,
 * {@code boolean} does not accept {@code "true"} in quotes, and so on. Wrong types and values out of
 * range are reported as errors.
 */
@NullMarked
public final class TaskKeyParsers {

    private TaskKeyParsers() {
    }

    // -------------------------------------------------------------------------------------------
    // boolean
    // -------------------------------------------------------------------------------------------

    public static TaskKeyParser<Boolean> booleanValue() {
        return (raw, context) -> {
            if (raw instanceof final Boolean b) {
                return b;
            }

            context.error("Expected a boolean for '" + context.getPath() + "', but got '" + raw + "' instead");
            return null;
        };
    }

    public static TaskKeyParser<List<Boolean>> booleanList() {
        return list(booleanValue());
    }

    // -------------------------------------------------------------------------------------------
    // byte
    // -------------------------------------------------------------------------------------------

    public static TaskKeyParser<Byte> byteValue() {
        return byteValue(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public static TaskKeyParser<Byte> byteValue(final byte min, final byte max) {
        return integral("a byte", min, max, value -> (byte) value);
    }

    public static TaskKeyParser<List<Byte>> byteList() {
        return list(byteValue());
    }

    public static TaskKeyParser<List<Byte>> byteList(final byte min, final byte max) {
        return list(byteValue(min, max));
    }

    /**
     * Like {@link #byteValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Byte> byteValuePositive() {
        return byteValue((byte) 1, Byte.MAX_VALUE);
    }

    /**
     * Like {@link #byteValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Byte> byteValueNegative() {
        return byteValue(Byte.MIN_VALUE, (byte) -1);
    }

    /**
     * Like {@link #byteValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Byte> byteValueNonPositive() {
        return byteValue(Byte.MIN_VALUE, (byte) 0);
    }

    /**
     * Like {@link #byteValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Byte> byteValueNonNegative() {
        return byteValue((byte) 0, Byte.MAX_VALUE);
    }

    /**
     * Like {@link #byteList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Byte>> byteListPositive() {
        return byteList((byte) 1, Byte.MAX_VALUE);
    }

    /**
     * Like {@link #byteList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Byte>> byteListNegative() {
        return byteList(Byte.MIN_VALUE, (byte) -1);
    }

    /**
     * Like {@link #byteList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Byte>> byteListNonPositive() {
        return byteList(Byte.MIN_VALUE, (byte) 0);
    }

    /**
     * Like {@link #byteList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Byte>> byteListNonNegative() {
        return byteList((byte) 0, Byte.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // short
    // -------------------------------------------------------------------------------------------

    public static TaskKeyParser<Short> shortValue() {
        return shortValue(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public static TaskKeyParser<Short> shortValue(final short min, final short max) {
        return integral("a short", min, max, value -> (short) value);
    }

    public static TaskKeyParser<List<Short>> shortList() {
        return list(shortValue());
    }

    public static TaskKeyParser<List<Short>> shortList(final short min, final short max) {
        return list(shortValue(min, max));
    }

    /**
     * Like {@link #shortValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Short> shortValuePositive() {
        return shortValue((short) 1, Short.MAX_VALUE);
    }

    /**
     * Like {@link #shortValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Short> shortValueNegative() {
        return shortValue(Short.MIN_VALUE, (short) -1);
    }

    /**
     * Like {@link #shortValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Short> shortValueNonPositive() {
        return shortValue(Short.MIN_VALUE, (short) 0);
    }

    /**
     * Like {@link #shortValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Short> shortValueNonNegative() {
        return shortValue((short) 0, Short.MAX_VALUE);
    }

    /**
     * Like {@link #shortList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Short>> shortListPositive() {
        return shortList((short) 1, Short.MAX_VALUE);
    }

    /**
     * Like {@link #shortList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Short>> shortListNegative() {
        return shortList(Short.MIN_VALUE, (short) -1);
    }

    /**
     * Like {@link #shortList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Short>> shortListNonPositive() {
        return shortList(Short.MIN_VALUE, (short) 0);
    }

    /**
     * Like {@link #shortList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Short>> shortListNonNegative() {
        return shortList((short) 0, Short.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // int
    // -------------------------------------------------------------------------------------------

    public static TaskKeyParser<Integer> integerValue() {
        return integerValue(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static TaskKeyParser<Integer> integerValue(final int min, final int max) {
        return integral("an integer", min, max, value -> (int) value);
    }

    public static TaskKeyParser<List<Integer>> integerList() {
        return list(integerValue());
    }

    public static TaskKeyParser<List<Integer>> integerList(final int min, final int max) {
        return list(integerValue(min, max));
    }

    /**
     * Like {@link #integerValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Integer> integerValuePositive() {
        return integerValue(1, Integer.MAX_VALUE);
    }

    /**
     * Like {@link #integerValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Integer> integerValueNegative() {
        return integerValue(Integer.MIN_VALUE, -1);
    }

    /**
     * Like {@link #integerValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Integer> integerValueNonPositive() {
        return integerValue(Integer.MIN_VALUE, 0);
    }

    /**
     * Like {@link #integerValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Integer> integerValueNonNegative() {
        return integerValue(0, Integer.MAX_VALUE);
    }

    /**
     * Like {@link #integerList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Integer>> integerListPositive() {
        return integerList(1, Integer.MAX_VALUE);
    }

    /**
     * Like {@link #integerList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Integer>> integerListNegative() {
        return integerList(Integer.MIN_VALUE, -1);
    }

    /**
     * Like {@link #integerList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Integer>> integerListNonPositive() {
        return integerList(Integer.MIN_VALUE, 0);
    }

    /**
     * Like {@link #integerList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Integer>> integerListNonNegative() {
        return integerList(0, Integer.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // long
    // -------------------------------------------------------------------------------------------

    public static TaskKeyParser<Long> longValue() {
        return longValue(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static TaskKeyParser<Long> longValue(final long min, final long max) {
        return integral("a long", min, max, value -> value);
    }

    public static TaskKeyParser<List<Long>> longList() {
        return list(longValue());
    }

    public static TaskKeyParser<List<Long>> longList(final long min, final long max) {
        return list(longValue(min, max));
    }

    /**
     * Like {@link #longValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Long> longValuePositive() {
        return longValue(1L, Long.MAX_VALUE);
    }

    /**
     * Like {@link #longValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Long> longValueNegative() {
        return longValue(Long.MIN_VALUE, -1L);
    }

    /**
     * Like {@link #longValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Long> longValueNonPositive() {
        return longValue(Long.MIN_VALUE, 0L);
    }

    /**
     * Like {@link #longValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Long> longValueNonNegative() {
        return longValue(0L, Long.MAX_VALUE);
    }

    /**
     * Like {@link #longList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Long>> longListPositive() {
        return longList(1L, Long.MAX_VALUE);
    }

    /**
     * Like {@link #longList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Long>> longListNegative() {
        return longList(Long.MIN_VALUE, -1L);
    }

    /**
     * Like {@link #longList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Long>> longListNonPositive() {
        return longList(Long.MIN_VALUE, 0L);
    }

    /**
     * Like {@link #longList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Long>> longListNonNegative() {
        return longList(0L, Long.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // float
    // -------------------------------------------------------------------------------------------

    /**
     * Accepts any finite number which fits in a {@code float}. Integral YAML values are accepted too.
     */
    public static TaskKeyParser<Float> floatValue() {
        return floatValue(-Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public static TaskKeyParser<Float> floatValue(final float min, final float max) {
        return floating("a float", min, max, value -> (float) value);
    }

    public static TaskKeyParser<List<Float>> floatList() {
        return list(floatValue());
    }

    public static TaskKeyParser<List<Float>> floatList(final float min, final float max) {
        return list(floatValue(min, max));
    }

    /**
     * Like {@link #floatValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Float> floatValuePositive() {
        return floatValue(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    /**
     * Like {@link #floatValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Float> floatValueNegative() {
        return floatValue(-Float.MAX_VALUE, -Float.MIN_VALUE);
    }

    /**
     * Like {@link #floatValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Float> floatValueNonPositive() {
        return floatValue(-Float.MAX_VALUE, 0.0f);
    }

    /**
     * Like {@link #floatValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Float> floatValueNonNegative() {
        return floatValue(0.0f, Float.MAX_VALUE);
    }

    /**
     * Like {@link #floatList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Float>> floatListPositive() {
        return floatList(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    /**
     * Like {@link #floatList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Float>> floatListNegative() {
        return floatList(-Float.MAX_VALUE, -Float.MIN_VALUE);
    }

    /**
     * Like {@link #floatList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Float>> floatListNonPositive() {
        return floatList(-Float.MAX_VALUE, 0.0f);
    }

    /**
     * Like {@link #floatList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Float>> floatListNonNegative() {
        return floatList(0.0f, Float.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // double
    // -------------------------------------------------------------------------------------------

    /**
     * Accepts any finite number. Integral YAML values are accepted too.
     */
    public static TaskKeyParser<Double> doubleValue() {
        return doubleValue(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public static TaskKeyParser<Double> doubleValue(final double min, final double max) {
        return floating("a double", min, max, value -> value);
    }

    public static TaskKeyParser<List<Double>> doubleList() {
        return list(doubleValue());
    }

    public static TaskKeyParser<List<Double>> doubleList(final double min, final double max) {
        return list(doubleValue(min, max));
    }

    /**
     * Like {@link #doubleValue()} but only accepts values > 0.
     */
    public static TaskKeyParser<Double> doubleValuePositive() {
        return doubleValue(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Like {@link #doubleValue()} but only accepts values < 0.
     */
    public static TaskKeyParser<Double> doubleValueNegative() {
        return doubleValue(-Double.MAX_VALUE, -Double.MIN_VALUE);
    }

    /**
     * Like {@link #doubleValue()} but only accepts values <= 0.
     */
    public static TaskKeyParser<Double> doubleValueNonPositive() {
        return doubleValue(-Double.MAX_VALUE, 0.0);
    }

    /**
     * Like {@link #doubleValue()} but only accepts values >= 0.
     */
    public static TaskKeyParser<Double> doubleValueNonNegative() {
        return doubleValue(0.0, Double.MAX_VALUE);
    }

    /**
     * Like {@link #doubleList()} but only accepts values > 0.
     */
    public static TaskKeyParser<List<Double>> doubleListPositive() {
        return doubleList(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Like {@link #doubleList()} but only accepts values < 0.
     */
    public static TaskKeyParser<List<Double>> doubleListNegative() {
        return doubleList(-Double.MAX_VALUE, -Double.MIN_VALUE);
    }

    /**
     * Like {@link #doubleList()} but only accepts values <= 0.
     */
    public static TaskKeyParser<List<Double>> doubleListNonPositive() {
        return doubleList(-Double.MAX_VALUE, 0.0);
    }

    /**
     * Like {@link #doubleList()} but only accepts values >= 0.
     */
    public static TaskKeyParser<List<Double>> doubleListNonNegative() {
        return doubleList(0.0, Double.MAX_VALUE);
    }

    // -------------------------------------------------------------------------------------------
    // char
    // -------------------------------------------------------------------------------------------

    /**
     * Accepts a string of exactly one character.
     */
    public static TaskKeyParser<Character> charValue() {
        return charValue(Character.MIN_VALUE, Character.MAX_VALUE);
    }

    public static TaskKeyParser<Character> charValue(final char min, final char max) {
        checkRange(min, max);

        return (raw, context) -> {
            final Character c;
            if (raw instanceof final Character character) {
                c = character;
            } else if (raw instanceof final String string && string.length() == 1) {
                c = string.charAt(0);
            } else {
                context.error("Expected a single character for '" + context.getPath() + "', but got '" + raw + "' instead");
                return null;
            }

            if (c < min || c > max) {
                context.error("Expected a character between '" + min + "' and '" + max + "' for '" + context.getPath() + "', but got '" + c + "' instead");
                return null;
            }

            return c;
        };
    }

    public static TaskKeyParser<List<Character>> charList() {
        return list(charValue());
    }

    public static TaskKeyParser<List<Character>> charList(final char min, final char max) {
        return list(charValue(min, max));
    }

    // -------------------------------------------------------------------------------------------
    // String
    // -------------------------------------------------------------------------------------------

    /**
     * Accepts any scalar value (string, number, boolean) and converts it to a string.
     */
    public static TaskKeyParser<String> stringValue() {
        return (raw, context) -> {
            if (isScalar(raw)) {
                return String.valueOf(raw);
            }

            context.error("Expected a string for '" + context.getPath() + "', but got '" + raw + "' instead");
            return null;
        };
    }

    public static TaskKeyParser<List<String>> stringList() {
        return list(stringValue());
    }

    // -------------------------------------------------------------------------------------------
    // enum and accepted values
    // -------------------------------------------------------------------------------------------

    /**
     * Parses a constant of the given enum by its exact name. Unknown values are reported as warnings.
     */
    public static <E extends Enum<E>> TaskKeyParser<E> enumValue(final Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass cannot be null");

        final Map<String, E> constants = new LinkedHashMap<>();
        for (final E constant : enumClass.getEnumConstants()) {
            constants.put(constant.name(), constant);
        }

        return acceptedValue(constants);
    }

    /**
     * Parses one or a list of constants of the given enum by their exact name.
     */
    public static <E extends Enum<E>> TaskKeyParser<List<E>> enumList(final Class<E> enumClass) {
        return list(enumValue(enumClass));
    }

    /**
     * Maps an accepted (exact, case-sensitive) string to a value.
     * Unknown values are reported as warnings listing all accepted values.
     *
     * @param acceptedValues map of accepted string to value; iteration order is used in the problem description
     */
    public static <V> TaskKeyParser<V> acceptedValue(final Map<String, V> acceptedValues) {
        Objects.requireNonNull(acceptedValues, "acceptedValues cannot be null");

        final Map<String, V> copy = Collections.unmodifiableMap(new LinkedHashMap<>(acceptedValues));

        return (raw, context) -> {
            if (!(raw instanceof final String string)) {
                context.warning("Expected a string for '" + context.getPath() + "', but got '" + raw + "' instead");
                return null;
            }

            final V value = copy.get(string);
            if (value == null) {
                final StringBuilder extended = new StringBuilder("The accepted values are:");
                for (final String accepted : copy.keySet()) {
                    extended.append("<br> - ").append(accepted);
                }

                context.report(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.NOT_ACCEPTED_VALUE.getDescription(string, context.getTaskType()),
                        extended.toString());
            }

            return value;
        };
    }

    /**
     * Like {@link #acceptedValue(Map)} but for plain strings.
     */
    public static TaskKeyParser<String> acceptedString(final Collection<String> acceptedValues) {
        Objects.requireNonNull(acceptedValues, "acceptedValues cannot be null");

        final Map<String, String> map = new LinkedHashMap<>();
        for (final String value : acceptedValues) {
            map.put(value, value);
        }

        return acceptedValue(map);
    }

    // -------------------------------------------------------------------------------------------
    // Collections
    // -------------------------------------------------------------------------------------------

    /**
     * Accepts either a single value or a list of values; every element is parsed by
     * {@code elementParser}. Elements for which the element parser returns null are skipped
     * (the element parser is responsible for reporting them).
     *
     * @param elementParser parser applied to each element
     * @param <E>           element type
     * @return parser producing an unmodifiable list in configuration order
     */
    public static <E> TaskKeyParser<List<E>> list(final TaskKeyParser<E> elementParser) {
        Objects.requireNonNull(elementParser, "elementParser cannot be null");

        return (raw, context) -> {
            final List<?> rawList = raw instanceof final List<?> l ? l : List.of(raw);
            final List<E> result = new ArrayList<>(rawList.size());

            for (final Object element : rawList) {
                if (element == null) {
                    continue;
                }

                final E parsed = elementParser.parse(element, context);

                if (parsed != null) {
                    result.add(parsed);
                }
            }

            return Collections.unmodifiableList(result);
        };
    }

    /**
     * Like {@link #list(TaskKeyParser)}, but duplicates are removed.
     *
     * @param elementParser parser applied to each element
     * @param <E>           element type
     * @return parser producing an unmodifiable set in configuration order
     */
    public static <E> TaskKeyParser<Set<E>> set(final TaskKeyParser<E> elementParser) {
        return list(elementParser).map(list -> Collections.unmodifiableSet(new LinkedHashSet<>(list)));
    }

    // -------------------------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------------------------

    /**
     * Parser for integral types; accepts {@code Byte}, {@code Short}, {@code Integer}, {@code Long}
     * and {@code BigInteger} values (as produced by YAML) within {@code [min, max]}.
     */
    private static <T> TaskKeyParser<T> integral(final String typeName, final long min, final long max, final LongFunction<T> converter) {
        checkRange(min, max);

        return (raw, context) -> {
            final long value;
            if (raw instanceof Byte || raw instanceof Short || raw instanceof Integer || raw instanceof Long) {
                value = ((Number) raw).longValue();
            } else if (raw instanceof final BigInteger bigInteger && bigInteger.bitLength() < Long.SIZE) {
                value = bigInteger.longValue();
            } else if (raw instanceof BigInteger) {
                outOfRange(context, typeName, min, max, raw);
                return null;
            } else {
                context.error("Expected " + typeName + " for '" + context.getPath() + "', but got '" + raw + "' instead");
                return null;
            }

            if (value < min || value > max) {
                outOfRange(context, typeName, min, max, raw);
                return null;
            }

            return converter.apply(value);
        };
    }

    /**
     * Parser for floating point types; accepts any {@code Number} which is finite and within {@code [min, max]}.
     */
    private static <T> TaskKeyParser<T> floating(final String typeName, final double min, final double max, final DoubleFunction<T> converter) {
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new IllegalArgumentException("min and max cannot be NaN");
        }
        if (min > max) {
            throw new IllegalArgumentException("min (" + min + ") cannot be greater than max (" + max + ")");
        }

        return (raw, context) -> {
            if (!(raw instanceof final Number number)) {
                context.error("Expected " + typeName + " for '" + context.getPath() + "', but got '" + raw + "' instead");
                return null;
            }

            final double value = number.doubleValue();
            if (Double.isNaN(value) || value < min || value > max) {
                outOfRange(context, typeName, min, max, raw);
                return null;
            }

            return converter.apply(value);
        };
    }

    private static void outOfRange(final TaskKeyContext context, final String typeName, final Object min, final Object max, final Object raw) {
        context.error("Expected " + typeName + " between " + min + " and " + max + " for '" + context.getPath() + "', but got '" + raw + "' instead");
    }

    private static void checkRange(final long min, final long max) {
        if (min > max) {
            throw new IllegalArgumentException("min (" + min + ") cannot be greater than max (" + max + ")");
        }
    }

    private static boolean isScalar(final @Nullable Object raw) {
        return raw instanceof String || raw instanceof Number || raw instanceof Boolean || raw instanceof Character;
    }
}
