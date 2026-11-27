package com.leonardobishop.quests.bukkit.tasktype.type.farming;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum FarmingMode {
    BREAK,
    HARVEST;

    static final Map<String, FarmingMode> STRING_MODE_MAP = new HashMap<>() {{
        for (final FarmingMode mode : FarmingMode.values()) {
            this.put(mode.name().toLowerCase(Locale.ROOT), mode);
        }
    }};
}
