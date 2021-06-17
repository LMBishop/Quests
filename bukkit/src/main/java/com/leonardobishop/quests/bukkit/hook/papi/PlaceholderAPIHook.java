package com.leonardobishop.quests.bukkit.hook.papi;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook implements AbstractPlaceholderAPIHook {

    private QuestsPlaceholders placeholder;

    public String replacePlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public void registerExpansion(BukkitQuestsPlugin plugin) {
        placeholder = new QuestsPlaceholders(plugin);
        placeholder.register();
    }

    @Override
    public void unregisterExpansion() {
        placeholder.unregister();
    }


}
