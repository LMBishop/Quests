package com.leonardobishop.quests.common.tasktype.key;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.quest.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TaskSchemaTest {

    enum Mode {FAST, SLOW}

    static final TaskKey<Integer> AMOUNT = TaskKey.of("amount", TaskKeyParsers.integerValue()).required();
    static final TaskKey<Boolean> FLAG = TaskKey.of("flag", TaskKeyParsers.booleanValue()).withDefault(true);
    static final TaskKey<List<String>> NAMES = TaskKey.of("name", TaskKeyParsers.stringList()).withAliases("names");
    static final TaskKey<Mode> MODE = TaskKey.of("mode", TaskKeyParsers.enumValue(Mode.class));
    static final TaskKey<Integer> UNREGISTERED = TaskKey.of("other", TaskKeyParsers.integerValue());

    static final class TestTask extends Task {

        static final TaskSchema<TestTask> SCHEMA = TaskSchema.builder(TestTask::new)
                .keys(AMOUNT, FLAG, NAMES, MODE)
                .build();

        final int amount;
        final boolean flag;
        final List<String> names;
        final Mode mode;

        TestTask(final String id, final String type, final TaskOptions options) {
            super(id, type, options);
            this.amount = options.get(AMOUNT);
            this.flag = options.get(FLAG);
            this.names = options.getOrNull(NAMES);
            this.mode = options.getOrNull(MODE);
        }

        @Override
        public Object getGoal() {
            return this.amount;
        }
    }

    private static TestTask load(final Map<String, Object> raw, final List<ConfigProblem> problems) {
        return TestTask.SCHEMA.load("t1", "test", raw, problems);
    }

    @Test
    void parsesValuesOnceIntoFields() {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("type", "test");
        raw.put("amount", 5);
        raw.put("names", List.of("a", "b"));
        raw.put("mode", "SLOW");

        final List<ConfigProblem> problems = new ArrayList<>();
        final TestTask task = load(raw, problems);

        assertNotNull(task);
        assertTrue(problems.isEmpty(), problems.toString());
        assertEquals(5, task.amount);
        assertTrue(task.flag, "default value should be used");
        assertEquals(List.of("a", "b"), task.names, "alias path should be read");
        assertEquals(Mode.SLOW, task.mode);
        assertEquals(5, task.getGoal());
        assertTrue(task.getOptions().has(AMOUNT));
        assertFalse(task.getOptions().has(FLAG));
    }

    @Test
    void singleStringBecomesList() {
        final List<ConfigProblem> problems = new ArrayList<>();
        final TestTask task = load(Map.of("amount", 1, "name", "x"), problems);

        assertNotNull(task);
        assertEquals(List.of("x"), task.names);
    }

    @Test
    void missingRequiredKeyIsAnError() {
        final List<ConfigProblem> problems = new ArrayList<>();
        final TestTask task = load(Map.of("flag", false), problems);

        assertNull(task);
        assertEquals(1, problems.size());
        assertEquals(ConfigProblem.ConfigProblemType.ERROR, problems.getFirst().getType());
        assertEquals("amount", problems.getFirst().getLocation());
    }

    @Test
    void wrongTypeIsAnError() {
        final List<ConfigProblem> problems = new ArrayList<>();
        final TestTask task = load(Map.of("amount", "five"), problems);

        assertNull(task);
        assertEquals(ConfigProblem.ConfigProblemType.ERROR, problems.getFirst().getType());
    }

    @Test
    void unknownEnumValueIsAWarning() {
        final List<ConfigProblem> problems = new ArrayList<>();
        final TestTask task = load(Map.of("amount", 1, "mode", "MEDIUM"), problems);

        assertNotNull(task);
        assertNull(task.mode);
        assertEquals(1, problems.size());
        assertEquals(ConfigProblem.ConfigProblemType.WARNING, problems.getFirst().getType());
        assertEquals("mode", problems.getFirst().getLocation());
    }

    @Test
    void unregisteredKeyIsRejected() {
        final TestTask task = load(Map.of("amount", 1), new ArrayList<>());

        assertNotNull(task);
        assertThrows(IllegalArgumentException.class, () -> task.getOptions().getOrNull(UNREGISTERED));
    }

    @Test
    void validatorRunsAfterParsing() {
        final TaskSchema<TestTask> schema = TaskSchema.builder(TestTask::new)
                .keys(AMOUNT, FLAG, NAMES, MODE)
                .validator((options, problems) -> {
                    if (options.get(AMOUNT) > 10) {
                        problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.ERROR, "too big", null, "amount"));
                    }
                })
                .build();

        final List<ConfigProblem> problems = new ArrayList<>();
        assertNull(schema.load("t", "test", Map.of("amount", 11), problems));
        assertEquals("too big", problems.getFirst().getDescription());
    }

    @Test
    void acceptsPrimaryPathsAndAliasesOnly() {
        assertTrue(TestTask.SCHEMA.acceptsPath("amount"));
        assertTrue(TestTask.SCHEMA.acceptsPath("name"));
        assertTrue(TestTask.SCHEMA.acceptsPath("names"));
        assertFalse(TestTask.SCHEMA.acceptsPath("amuont"));
        assertFalse(TestTask.SCHEMA.acceptsPath("other"));
        assertEquals(List.of("amount", "flag", "name", "names", "mode"), List.copyOf(TestTask.SCHEMA.getPaths()));
    }

    @Test
    void optionsAreImmutableCopies() {
        final Map<TaskKey<?>, Object> values = new HashMap<>();
        values.put(AMOUNT, 3);
        final TaskOptions options = new TaskOptions(new HashSet<>(List.of(AMOUNT, FLAG)), values);

        values.put(AMOUNT, 99);
        assertEquals(3, options.get(AMOUNT), "later changes to the source map must not leak in");
        assertThrows(UnsupportedOperationException.class, () -> options.asMap().put(FLAG, false));

        final TestTask task = load(Map.of("amount", 1), new ArrayList<>());
        assertNotNull(task);
        assertThrows(UnsupportedOperationException.class, () -> task.getOptions().asMap().clear());
    }

    @Test
    void duplicatePathIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> TaskSchema.builder(TestTask::new)
                .keys(AMOUNT, TaskKey.of("amount", TaskKeyParsers.integerValue())));
    }
}
