package com.leonardobishop.quests.bukkit.util.chat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface ColorAdapter {

    @Contract("null -> null")
    String color(@Nullable String s);

    @Contract("null -> null")
    String strip(@Nullable String s);
}
