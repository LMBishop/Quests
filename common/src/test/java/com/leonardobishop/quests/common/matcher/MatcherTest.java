package com.leonardobishop.quests.common.matcher;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyContext;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class MatcherTest {

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

    private static final TaskKeyParser<TextMatcher> TEXT = TextMatcher.parser(UnaryOperator.identity());
    private static final TaskKeyParser<List<LoreMatcher>> LORE = LoreMatcher.listParser(UnaryOperator.identity());

    private static Map<String, Object> map(final Object... kv) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put((String) kv[i], kv[i + 1]);
        }
        return map;
    }

    private static <T> T parse(final TaskKeyParser<T> parser, final Object raw) {
        final TestContext context = new TestContext();
        final T value = parser.parse(raw, context);
        assertTrue(context.problems.isEmpty(), () -> "unexpected problem: " + context.problems.getFirst().getDescription());
        assertNotNull(value);
        return value;
    }

    private static void assertRejected(final TaskKeyParser<?> parser, final Object raw) {
        final TestContext context = new TestContext();
        assertNull(parser.parse(raw, context));
        assertTrue(context.hasError());
    }

    // ---------------------------------------------------------------------------------------------
    // TextMatcher
    // ---------------------------------------------------------------------------------------------

    @Test
    void absentEmptyAndSpecificAreDistinct() {
        final TextMatcher absent = parse(TEXT, map("mode", "absent"));
        final TextMatcher present = parse(TEXT, map("mode", "present"));
        final TextMatcher empty = parse(TEXT, "");
        final TextMatcher sword = parse(TEXT, "Sword");

        assertTrue(absent.matches(null));
        assertFalse(absent.matches(""));

        assertFalse(present.matches(null));
        assertTrue(present.matches(""));
        assertTrue(present.matches("Sword"));

        assertFalse(empty.matches(null));
        assertTrue(empty.matches(""));
        assertFalse(empty.matches("Sword"));

        assertTrue(sword.matches("Sword"));
        assertFalse(sword.matches("sword"));
        assertFalse(sword.matches(null));
    }

    @Test
    void modesAndIgnoreCase() {
        assertTrue(parse(TEXT, map("text", "Leg", "mode", "starts-with")).matches("Legendary"));
        assertFalse(parse(TEXT, map("text", "leg", "mode", "starts-with")).matches("Legendary"));
        assertTrue(parse(TEXT, map("text", "leg", "mode", "starts-with", "ignore-case", true)).matches("Legendary"));

        assertTrue(parse(TEXT, map("text", "ARY", "mode", "ends-with", "ignore-case", true)).matches("Legendary"));
        assertFalse(parse(TEXT, map("text", "Legendary sword", "mode", "ends-with")).matches("sword"));

        assertTrue(parse(TEXT, map("text", "END", "mode", "contains", "ignore-case", true)).matches("Legendary"));
        assertFalse(parse(TEXT, map("text", "END", "mode", "contains")).matches("Legendary"));

        assertTrue(parse(TEXT, map("text", "sword", "ignore-case", true)).matches("SWORD"));
    }

    @Test
    void invalidTextMatchers() {
        assertRejected(TEXT, map("mode", "contains"));
        assertRejected(TEXT, map("mode", "absent", "text", "x"));
        assertRejected(TEXT, map("mode", "regex", "text", "x"));
        assertRejected(TEXT, map("text", "x", "ignore-case", "yes"));
        assertRejected(TEXT, List.of("a"));
    }

    @Test
    void textTransformerIsApplied() {
        final TextMatcher matcher = parse(TextMatcher.parser(s -> s.replace('&', '§')), "&6Sword");
        assertTrue(matcher.matches("§6Sword"));
    }

    @Test
    void exactness() {
        assertTrue(parse(TEXT, "x").isExact());
        assertTrue(parse(TEXT, map("mode", "absent")).isExact());
        assertFalse(parse(TEXT, map("mode", "present")).isExact());
        assertFalse(parse(TEXT, map("text", "x", "ignore-case", true)).isExact());
        assertFalse(parse(TEXT, map("text", "x", "mode", "contains")).isExact());
    }

    // ---------------------------------------------------------------------------------------------
    // LoreMatcher
    // ---------------------------------------------------------------------------------------------

    private static final List<String> ITEM_LORE = List.of("Rare", "Legendary sword", "Soulbound");

    @Test
    void flatListIsOneExactLore() {
        final List<LoreMatcher> lores = parse(LORE, List.of("Rare", map("text", "legendary", "mode", "contains", "ignore-case", true), "Soulbound"));
        assertEquals(1, lores.size());
        assertEquals(LoreMatcher.Mode.EXACT, lores.getFirst().getMode());
        assertTrue(lores.getFirst().matches(ITEM_LORE));
        assertFalse(lores.getFirst().matches(List.of("Rare", "Legendary sword")));
    }

    @Test
    void orderedAndAnyOrder() {
        final LoreMatcher ordered = parse(LORE, map("mode", "ordered", "lines", List.of("Rare", "Soulbound"))).getFirst();
        assertTrue(ordered.matches(ITEM_LORE));
        assertFalse(ordered.matches(List.of("Soulbound", "Rare")));

        final LoreMatcher anyOrder = parse(LORE, map("mode", "contains-all", "lines", List.of("Soulbound", "Rare"))).getFirst();
        assertTrue(anyOrder.matches(ITEM_LORE));
        assertFalse(anyOrder.matches(List.of("Rare")));
    }

    @Test
    void anyOrderNeedsDistinctLines() {
        final LoreMatcher lore = parse(LORE, map("mode", "contains-all", "lines", List.of(
                map("text", "a", "mode", "contains"),
                map("text", "b", "mode", "contains")))).getFirst();

        assertFalse(lore.matches(List.of("ab")), "one line cannot satisfy two matchers");
        assertTrue(lore.matches(List.of("ab", "b")), "needs reassignment: 'ab'->a, 'b'->b");
    }

    @Test
    void severalLoresAreAlternatives() {
        final List<LoreMatcher> lores = parse(LORE, List.of(
                List.of("A", "B"),
                map("mode", "contains-all", "lines", List.of("C"))));
        assertEquals(2, lores.size());
        assertTrue(lores.stream().anyMatch(l -> l.matches(List.of("A", "B"))));
        assertTrue(lores.stream().anyMatch(l -> l.matches(List.of("x", "C"))));
        assertFalse(lores.stream().anyMatch(l -> l.matches(List.of("A"))));
    }

    @Test
    void emptyListMeansNoLore() {
        final LoreMatcher lore = parse(LORE, List.of()).getFirst();
        assertTrue(lore.matches(null));
        assertTrue(lore.matches(List.of()));
        assertFalse(lore.matches(List.of("x")));
    }

    @Test
    void loreContainsAnyAndNone() {
        final LoreMatcher any = parse(LORE, map("mode", "contains-any", "lines", List.of("Rare", "Epic"))).getFirst();
        assertTrue(any.matches(ITEM_LORE));
        assertFalse(any.matches(List.of("Common")));
        assertFalse(any.matches(null));

        final LoreMatcher none = parse(LORE, map("mode", "none", "lines", List.of(map("text", "quest item", "mode", "contains", "ignore-case", true)))).getFirst();
        assertTrue(none.matches(ITEM_LORE));
        assertTrue(none.matches(null));
        assertFalse(none.matches(List.of("Common", "[Quest Item]")));
    }

    @Test
    void emptyLoreSetMeansNoLoreInEveryMode() {
        for (final String mode : List.of("exact", "ordered", "contains-all", "contains-any", "none")) {
            final LoreMatcher lore = parse(LORE, map("mode", mode, "lines", List.of())).getFirst();
            assertTrue(lore.matches(null), mode);
            assertFalse(lore.matches(List.of("x")), mode);
        }
    }

    @Test
    void containsAllNeedsADifferentLinePerCondition() {
        final LoreMatcher lore = parse(LORE, map("mode", "contains-all", "lines", List.of(
                map("text", "Legendary", "mode", "starts-with"),
                map("text", "sword", "mode", "ends-with")))).getFirst();
        assertFalse(lore.matches(List.of("Legendary sword", "Rare")));
        assertTrue(lore.matches(List.of("Legendary axe", "Old sword")));
    }

    @Test
    void mixingLinesAndLoresIsAnError() {
        assertRejected(LORE, Arrays.asList("A", List.of("B")));
    }

    @Test
    void loreExactness() {
        assertTrue(parse(LORE, List.of("A", "B")).getFirst().isExact());
        assertFalse(parse(LORE, map("mode", "ordered", "lines", List.of("A"))).getFirst().isExact());
        assertFalse(parse(LORE, List.of(map("text", "A", "mode", "contains"))).getFirst().isExact());
    }
}
