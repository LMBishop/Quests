package com.leonardobishop.quests.common.matcher;

import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The enchantment examples agreed on for item requirements, using plain names instead of Bukkit enchantments.
 */
public final class CollectionMatcherTest {

    record Ench(String name, LevelRange levels) implements CollectionMatcher.Condition<String, Integer> {
        @Override
        public String key() {
            return this.name;
        }

        @Override
        public boolean test(final Integer level) {
            return this.levels.contains(level);
        }
    }

    static final TaskKeyParser<Ench> ENCH = (raw, context) -> {
        final String value = String.valueOf(raw);
        final int colon = value.lastIndexOf(':');
        if (colon < 0) {
            return new Ench(value, LevelRange.PRESENT);
        }
        final LevelRange levels = LevelRange.parse(value.substring(colon + 1));
        if (levels == null) {
            context.error("bad level");
            return null;
        }
        return new Ench(value.substring(0, colon), levels);
    };

    static final TaskKeyParser<List<CollectionMatcher<String, Integer>>> ENCHANTMENTS = CollectionMatcher.listParser(ENCH,
            CollectionMatcher.Mode.CONTAINS_ALL, EnumSet.of(CollectionMatcher.Mode.CONTAINS_ALL, CollectionMatcher.Mode.CONTAINS_ANY, CollectionMatcher.Mode.EXACT));

    private static List<CollectionMatcher<String, Integer>> parse(final Object raw) {
        final MatcherTest.TestContext context = new MatcherTest.TestContext();
        final List<CollectionMatcher<String, Integer>> sets = ENCHANTMENTS.parse(raw, context);
        assertTrue(context.problems.isEmpty(), () -> "unexpected problem: " + context.problems.getFirst().getDescription());
        assertNotNull(sets);
        return sets;
    }

    private static void assertRejected(final Object raw) {
        final MatcherTest.TestContext context = new MatcherTest.TestContext();
        assertNull(ENCHANTMENTS.parse(raw, context));
        assertTrue(context.hasError());
    }

    /** Item enchantments as "NAME:level" pairs. */
    private static boolean matches(final List<CollectionMatcher<String, Integer>> sets, final String... item) {
        final Map<String, Integer> levels = new LinkedHashMap<>();
        for (final String e : item) {
            final String[] parts = e.split(":");
            levels.put(parts[0], Integer.parseInt(parts[1]));
        }
        return sets.stream().anyMatch(set -> set.matches(k -> levels.getOrDefault(k, 0), levels.keySet()));
    }

    private static Map<String, Object> section(final String mode, final Object values) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("mode", mode);
        map.put("values", values);
        return map;
    }

    // ---------------------------------------------------------------------------------------------
    // LevelRange
    // ---------------------------------------------------------------------------------------------

    @Test
    void levelNotation() {
        assertEquals(new LevelRange(3, 3), LevelRange.parse("3"));
        assertEquals(new LevelRange(3, null), LevelRange.parse("3+"));
        assertEquals(new LevelRange(0, 3), LevelRange.parse("3-"));
        assertEquals(new LevelRange(3, 5), LevelRange.parse("3-5"));
        assertEquals(new LevelRange(0, 0), LevelRange.parse("0"));
        assertNull(LevelRange.parse("5-3"));
        assertNull(LevelRange.parse("-3"));
        assertNull(LevelRange.parse("x"));

        assertTrue(LevelRange.parse("3-").contains(0), "3- includes absence");
        assertFalse(LevelRange.PRESENT.contains(0));
        assertTrue(LevelRange.parse("0+").isAlwaysTrue());
        assertTrue(LevelRange.parse("5").isSingle());
    }

    // ---------------------------------------------------------------------------------------------
    // E1-E9
    // ---------------------------------------------------------------------------------------------

    @Test
    void e1_oneSetTwoConditions() {
        final var sets = parse(List.of("SHARPNESS:4+", "UNBREAKING"));
        assertTrue(matches(sets, "SHARPNESS:5", "UNBREAKING:1"));
        assertTrue(matches(sets, "SHARPNESS:4", "UNBREAKING:3", "MENDING:1"));
        assertFalse(matches(sets, "SHARPNESS:3", "UNBREAKING:3"));
        assertFalse(matches(sets, "SHARPNESS:5"));
    }

    @Test
    void e2_presenceAndAbsenceInOneSet() {
        final var sets = parse(List.of("SHARPNESS:5", "MENDING:0", "VANISHING_CURSE:0"));
        assertTrue(matches(sets, "SHARPNESS:5"));
        assertTrue(matches(sets, "SHARPNESS:5", "UNBREAKING:3"));
        assertFalse(matches(sets, "SHARPNESS:5", "MENDING:1"));
        assertFalse(matches(sets, "SHARPNESS:4"));
    }

    @Test
    void e3_atMostIncludesAbsence() {
        final var sets = parse(List.of("SHARPNESS:2-", "SMITE:2-", "BANE_OF_ARTHROPODS:2-"));
        assertTrue(matches(sets));
        assertTrue(matches(sets, "SHARPNESS:2", "SMITE:1"));
        assertTrue(matches(sets, "UNBREAKING:3"));
        assertFalse(matches(sets, "SHARPNESS:3"));
    }

    @Test
    void e4_containsAny() {
        final var sets = parse(section("contains-any", List.of("LOOTING:2+", "FORTUNE:2+", "SILK_TOUCH")));
        assertTrue(matches(sets, "FORTUNE:3"));
        assertTrue(matches(sets, "SILK_TOUCH:1", "EFFICIENCY:5"));
        assertFalse(matches(sets, "LOOTING:1"));
        assertFalse(matches(sets, "EFFICIENCY:5"));
    }

    @Test
    void e5_exact() {
        final var sets = parse(section("exact", List.of("EFFICIENCY:4+", "UNBREAKING:3")));
        assertTrue(matches(sets, "EFFICIENCY:5", "UNBREAKING:3"));
        assertTrue(matches(sets, "EFFICIENCY:4", "UNBREAKING:3"));
        assertFalse(matches(sets, "EFFICIENCY:5", "UNBREAKING:3", "MENDING:1"));
        assertFalse(matches(sets, "EFFICIENCY:5"));
    }

    @Test
    void e6_alternativeSets() {
        final var sets = parse(List.of(List.of("SHARPNESS:5", "FIRE_ASPECT:2"), List.of("SMITE:5", "LOOTING:3")));
        assertEquals(2, sets.size());
        assertTrue(matches(sets, "SHARPNESS:5", "FIRE_ASPECT:2", "LOOTING:3"));
        assertTrue(matches(sets, "SMITE:5", "LOOTING:3"));
        assertFalse(matches(sets, "SHARPNESS:5", "LOOTING:3"));
        assertFalse(matches(sets, "SMITE:5", "FIRE_ASPECT:2"));
    }

    @Test
    void e7_alternativeSetsWithModes() {
        final var sets = parse(List.of(
                List.of(),
                section("exact", List.of("UNBREAKING")),
                section("contains-any", List.of("BINDING_CURSE", "VANISHING_CURSE"))));
        assertTrue(matches(sets));
        assertTrue(matches(sets, "UNBREAKING:2"));
        assertFalse(matches(sets, "UNBREAKING:2", "MENDING:1"));
        assertTrue(matches(sets, "SHARPNESS:5", "VANISHING_CURSE:1"));
    }

    @Test
    void e8_mixingValuesAndSetsIsAnError() {
        assertRejected(Arrays.asList("SHARPNESS:5", List.of("FIRE_ASPECT:2")));
    }

    @Test
    void e9_sameKeyTwiceInOneSetIsAnError() {
        assertRejected(List.of("SHARPNESS:3+", "SHARPNESS:5-"));
        // ...but fine in different sets
        assertEquals(2, parse(List.of(List.of("SHARPNESS:3+"), List.of("SHARPNESS:1"))).size());
    }

    @Test
    void emptySetMeansNoneInEveryMode() {
        for (final String mode : List.of("contains-all", "contains-any", "exact")) {
            final var sets = parse(section(mode, List.of()));
            assertTrue(matches(sets), mode);
            assertFalse(matches(sets, "UNBREAKING:1"), mode);
        }
    }

    @Test
    void disallowedModeIsAnError() {
        assertRejected(section("none", List.of("MENDING")));
    }

    @Test
    void singleValueIsOneSet() {
        final var sets = parse("SHARPNESS:3+");
        assertEquals(1, sets.size());
        assertTrue(matches(sets, "SHARPNESS:4"));
        assertFalse(matches(sets, "SHARPNESS:2"));
    }
}
