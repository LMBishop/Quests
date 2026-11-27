package com.leonardobishop.quests.common.tasktype.key;

import com.leonardobishop.quests.common.config.ConfigProblem;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TaskKeyParsersTest {

    enum Color { RED, GREEN }

    /** Minimal context collecting reported problems. */
    static final class TestContext implements TaskKeyContext {

        final List<ConfigProblem> problems = new ArrayList<>();

        @Override
        public String getTaskType() {
            return "test";
        }

        @Override
        public String getPath() {
            return "key";
        }

        @Override
        public Object getSibling(final String path) {
            return null;
        }

        @Override
        public void report(final ConfigProblem.ConfigProblemType type, final String description, final String extendedDescription) {
            this.problems.add(new ConfigProblem(type, description, extendedDescription, "key"));
        }

        @Override
        public void report(final ConfigProblem.ConfigProblemType type, final String description, final String extendedDescription, final String subPath) {
            this.problems.add(new ConfigProblem(type, description, extendedDescription, "key." + subPath));
        }

        boolean hasError() {
            return this.problems.stream().anyMatch(p -> p.getType() == ConfigProblem.ConfigProblemType.ERROR);
        }
    }

    private static <T> T parse(final TaskKeyParser<T> parser, final Object raw) {
        final TestContext context = new TestContext();
        final T value = parser.parse(raw, context);
        assertTrue(context.problems.isEmpty(), () -> "unexpected problems: " + context.problems.getFirst().getDescription());
        return value;
    }

    private static <T> void assertRejected(final TaskKeyParser<T> parser, final Object raw) {
        final TestContext context = new TestContext();
        assertNull(parser.parse(raw, context), "expected '" + raw + "' to be rejected");
        assertTrue(context.hasError(), "expected an error for '" + raw + "'");
    }

    @Test
    void integralTypesAcceptYamlIntegersInTheirRange() {
        assertEquals(5, parse(TaskKeyParsers.integerValue(), 5));
        assertEquals(5, parse(TaskKeyParsers.integerValue(), 5L));
        assertEquals(5L, parse(TaskKeyParsers.longValue(), 5));
        assertEquals(Long.MAX_VALUE, parse(TaskKeyParsers.longValue(), Long.MAX_VALUE));
        assertEquals((short) 7, parse(TaskKeyParsers.shortValue(), 7));
        assertEquals((byte) -3, parse(TaskKeyParsers.byteValue(), -3));
    }

    @Test
    void integralTypesRejectWrongTypesAndOverflow() {
        assertRejected(TaskKeyParsers.integerValue(), "5");
        assertRejected(TaskKeyParsers.integerValue(), 5.0);
        assertRejected(TaskKeyParsers.integerValue(), (long) Integer.MAX_VALUE + 1);
        assertRejected(TaskKeyParsers.byteValue(), 128);
        assertRejected(TaskKeyParsers.shortValue(), 40000);
        assertRejected(TaskKeyParsers.longValue(), BigInteger.TWO.pow(70));
        assertEquals(10L, parse(TaskKeyParsers.longValue(), BigInteger.TEN));
    }

    @Test
    void rangesAreInclusive() {
        final TaskKeyParser<Integer> positive = TaskKeyParsers.integerValue(1, 10);
        assertEquals(1, parse(positive, 1));
        assertEquals(10, parse(positive, 10));
        assertRejected(positive, 0);
        assertRejected(positive, 11);

        final TaskKeyParser<Double> percent = TaskKeyParsers.doubleValue(0.0, 100.0);
        assertEquals(100.0, parse(percent, 100));
        assertRejected(percent, 100.5);
        assertRejected(percent, -0.1);
    }

    @Test
    void signVariants() {
        assertEquals(1, parse(TaskKeyParsers.integerValuePositive(), 1));
        assertRejected(TaskKeyParsers.integerValuePositive(), 0);
        assertEquals(0, parse(TaskKeyParsers.integerValueNonNegative(), 0));
        assertRejected(TaskKeyParsers.integerValueNonNegative(), -1);
        assertEquals(-1L, parse(TaskKeyParsers.longValueNegative(), -1));
        assertRejected(TaskKeyParsers.longValueNegative(), 0);
        assertEquals((short) 0, parse(TaskKeyParsers.shortValueNonPositive(), 0));
        assertRejected(TaskKeyParsers.byteValueNonPositive(), 1);

        // floating point: "positive" excludes zero but accepts the smallest positive value
        assertEquals(Double.MIN_VALUE, parse(TaskKeyParsers.doubleValuePositive(), Double.MIN_VALUE));
        assertRejected(TaskKeyParsers.doubleValuePositive(), 0.0);
        assertRejected(TaskKeyParsers.floatValueNegative(), 0.0);
        assertEquals(-0.5f, parse(TaskKeyParsers.floatValueNegative(), -0.5));
        assertEquals(0.0, parse(TaskKeyParsers.doubleValueNonNegative(), 0));

        assertEquals(List.of(1, 2), parse(TaskKeyParsers.integerListPositive(), List.of(1, 2)));
        final TestContext context = new TestContext();
        assertEquals(List.of(3), TaskKeyParsers.integerListNonNegative().parse(List.of(-1, 3), context));
        assertTrue(context.hasError());
    }

    @Test
    void invalidRangeIsAProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> TaskKeyParsers.integerValue(5, 1));
        assertThrows(IllegalArgumentException.class, () -> TaskKeyParsers.doubleValue(Double.NaN, 1));
    }

    @Test
    void floatingTypesAcceptAnyFiniteNumber() {
        assertEquals(1.5, parse(TaskKeyParsers.doubleValue(), 1.5));
        assertEquals(3.0, parse(TaskKeyParsers.doubleValue(), 3));
        assertEquals(2.5f, parse(TaskKeyParsers.floatValue(), 2.5));
        assertRejected(TaskKeyParsers.doubleValue(), "1.5");
        assertRejected(TaskKeyParsers.doubleValue(), Double.POSITIVE_INFINITY);
        assertRejected(TaskKeyParsers.doubleValue(), Double.NaN);
        assertRejected(TaskKeyParsers.floatValue(), 1e300);
        // negative values are accepted by default (Float.MIN_VALUE is the smallest *positive* float)
        assertEquals(-2.0f, parse(TaskKeyParsers.floatValue(), -2));
    }

    @Test
    void booleanCharAndString() {
        assertEquals(true, parse(TaskKeyParsers.booleanValue(), true));
        assertRejected(TaskKeyParsers.booleanValue(), "true");

        assertEquals('x', parse(TaskKeyParsers.charValue(), "x"));
        assertRejected(TaskKeyParsers.charValue(), "xy");
        assertRejected(TaskKeyParsers.charValue('a', 'f'), "z");

        assertEquals("5", parse(TaskKeyParsers.stringValue(), 5));
        assertRejected(TaskKeyParsers.stringValue(), List.of("a"));
    }

    @Test
    void listsAcceptSingleValuesAndLists() {
        assertEquals(List.of(1), parse(TaskKeyParsers.integerList(), 1));
        assertEquals(List.of(1, 2, 3), parse(TaskKeyParsers.integerList(), List.of(1, 2, 3)));
        assertEquals(List.of("a", "b"), parse(TaskKeyParsers.stringList(), List.of("a", "b")));
        assertEquals(List.of(Color.GREEN), parse(TaskKeyParsers.enumList(Color.class), "GREEN"));
        assertEquals(List.of(1.0, 2.5), parse(TaskKeyParsers.doubleList(), List.of(1, 2.5)));

        final TestContext context = new TestContext();
        assertEquals(List.of(2), TaskKeyParsers.integerList(1, 5).parse(List.of(2, 9), context));
        assertTrue(context.hasError(), "out of range element should be reported");
    }

    @Test
    void setRemovesDuplicatesAndKeepsOrder() {
        final Set<String> set = parse(TaskKeyParsers.set(TaskKeyParsers.stringValue()), List.of("b", "a", "b"));
        assertEquals(List.of("b", "a"), List.copyOf(set));
        assertThrows(UnsupportedOperationException.class, () -> set.add("c"));
    }

    @Test
    void unknownEnumIsAWarning() {
        final TestContext context = new TestContext();
        assertNull(TaskKeyParsers.enumValue(Color.class).parse("BLUE", context));
        assertEquals(ConfigProblem.ConfigProblemType.WARNING, context.problems.getFirst().getType());
    }
}
