package com.leonardobishop.quests.bukkit.util.chat;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexColorAdapter implements ColorAdapter {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    @Override
    public String color(@Nullable String s) {
        if (s == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(s);
        while (matcher.find()) {
            String hexCode = matcher.group().substring(1);
            s = s.substring(0, matcher.start()) + ChatColor.of(hexCode) + s.substring(matcher.end());
            matcher = HEX_PATTERN.matcher(s);
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
