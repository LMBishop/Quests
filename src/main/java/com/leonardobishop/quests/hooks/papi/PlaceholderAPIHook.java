package com.leonardobishop.quests.hooks.papi;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.QuestsPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook implements IPlaceholderAPIHook {

    private QuestsPlaceholders placeholder;

    public String replacePlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public void registerExpansion(Quests plugin) {
        placeholder = new QuestsPlaceholders(plugin);
        placeholder.register();
    }


    @Override
    public void unregisterExpansion() {
        placeholder.unregister();
    }


}
