package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Base class of all Bukkit tasks. Holds the options understood by
 * {@link com.leonardobishop.quests.bukkit.util.constraint.TaskConstraint task constraints},
 * which every task accepts.
 */
@NullMarked
public abstract class BukkitTask extends Task {

    public static final TaskKey<Set<String>> WORLDS = TaskKey.of("worlds", TaskKeyParsers.set(TaskKeyParsers.stringValue()));
    public static final TaskKey<Set<String>> IGNORED_WORLDS = TaskKey.of("ignored-worlds", TaskKeyParsers.set(TaskKeyParsers.stringValue()));
    public static final TaskKey<Set<String>> BIOMES = TaskKey.of("biomes", TaskKeyParsers.set(TaskKeyParsers.stringValue()));

    private final @Nullable Set<String> worlds;
    private final @Nullable Set<String> ignoredWorlds;
    private final @Nullable Set<String> biomes;

    protected BukkitTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.worlds = options.getOrNull(WORLDS);
        this.ignoredWorlds = options.getOrNull(IGNORED_WORLDS);
        this.biomes = options.getOrNull(BIOMES);
    }

    /**
     * Starts a schema with the keys every Bukkit task accepts.
     */
    public static <T extends BukkitTask> TaskSchema.Builder<T> schema(final TaskSchema.Factory<T> factory) {
        return TaskSchema.builder(factory).keys(WORLDS, IGNORED_WORLDS, BIOMES);
    }

    /**
     * @param worldName world name
     * @return whether the world passes both {@code worlds} and {@code ignored-worlds}
     */
    public final boolean isWorldAllowed(final String worldName) {
        return (this.worlds == null || this.worlds.contains(worldName))
                && (this.ignoredWorlds == null || !this.ignoredWorlds.contains(worldName));
    }

    /**
     * @return configured biome keys, or null if the task is not restricted to biomes
     */
    public final @Nullable Set<String> getBiomes() {
        return this.biomes;
    }

    public final @Nullable Set<String> getWorlds() {
        return this.worlds;
    }

    public final @Nullable Set<String> getIgnoredWorlds() {
        return this.ignoredWorlds;
    }
}
