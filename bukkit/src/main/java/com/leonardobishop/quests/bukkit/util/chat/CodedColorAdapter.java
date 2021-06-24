package com.leonardobishop.quests.bukkit.util.chat;

import org.bukkit.ChatColor;

public class CodedColorAdapter implements ColorAdapter {

    @Override
    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public String strip(String s) {
        return ChatColor.stripColor(s);
    }

}
