package com.leonardobishop.quests.bukkit.hook.papi.util;

import com.leonardobishop.quests.common.util.Modern;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class EnumUtil {

    public static <E extends Enum<E> & Named> Map<String, E> namedMap(final Class<E> clazz) {
        final E[] constants = clazz.getEnumConstants();

        final Map<String, E> map = new HashMap<>(constants.length);

        for (final E constant : constants) {
            for (final String name : constant.getNames()) {
                if (map.put(name, constant) != null) {
                    throw new IllegalStateException("'" + name + "' already bound");
                }
            }
        }

        return Collections.unmodifiableMap(map);
    }

    public interface Named {

        List<String> getNames();
    }
}
