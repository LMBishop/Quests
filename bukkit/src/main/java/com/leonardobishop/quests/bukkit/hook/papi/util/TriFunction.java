package com.leonardobishop.quests.bukkit.hook.papi.util;

import com.leonardobishop.quests.common.util.Modern;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Modern(type = Modern.Type.FULL)
@NullMarked
@FunctionalInterface
public interface TriFunction<T extends @Nullable Object, U extends @Nullable Object, V extends @Nullable Object, R extends @Nullable Object> {

    R apply(T t, U u, V v);
}
