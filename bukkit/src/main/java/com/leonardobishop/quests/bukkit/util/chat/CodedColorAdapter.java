package com.leonardobishop.quests.bukkit.util.chat;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public final class CodedColorAdapter implements ColorAdapter {

    @Override
    public String color(@Nullable String s) {
        if (s == null) {
            return null;
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public String strip(@Nullable String s) {
        if (s == null) {
            return null;
        }

        return ChatColor.stripColor(s);
    }
}
