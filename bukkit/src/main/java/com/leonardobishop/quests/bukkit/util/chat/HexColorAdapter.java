package com.leonardobishop.quests.bukkit.util.chat;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColorAdapter implements ColorAdapter {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public String color(String s) {
        if (s == null) return null;
        Matcher matcher = HEX_PATTERN.matcher(s);
        while (matcher.find()) {
            final ChatColor hexColor;
            try {
                hexColor = ChatColor.of(matcher.group().substring(1));
            } catch (IllegalArgumentException ex) {
                continue;
            }
            final String before = s.substring(0, matcher.start());
            final String after = s.substring(matcher.end());
            s = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public String strip(String s) {
        if (s == null) return null;
        return ChatColor.stripColor(s);
    }

}
