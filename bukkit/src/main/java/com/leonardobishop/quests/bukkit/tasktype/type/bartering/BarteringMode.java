package com.leonardobishop.quests.bukkit.tasktype.type.bartering;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum BarteringMode {
    INPUT,
    OUTPUT;

    static final Map<String, BarteringMode> STRING_MODE_MAP = new HashMap<>() {{
        for (final BarteringMode mode : BarteringMode.values()) {
            this.put(mode.name().toLowerCase(Locale.ROOT), mode);
        }
    }};
}
