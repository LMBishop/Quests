package com.leonardobishop.quests.hooks.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {

    public String replacePlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

}
