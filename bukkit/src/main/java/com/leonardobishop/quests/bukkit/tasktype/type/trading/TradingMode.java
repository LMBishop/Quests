package com.leonardobishop.quests.bukkit.tasktype.type.trading;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum TradingMode {
    RESULT,
    FIRST_INGREDIENT,
    SECOND_INGREDIENT,
    COUNT;

    static final Map<String, TradingMode> STRING_MODE_MAP = new HashMap<>() {{
        for (final TradingMode mode : TradingMode.values()) {
            this.put(mode.name().toLowerCase(Locale.ROOT), mode);
        }
    }};
}
