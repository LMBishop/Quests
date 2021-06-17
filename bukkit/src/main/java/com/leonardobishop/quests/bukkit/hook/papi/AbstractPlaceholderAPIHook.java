package com.leonardobishop.quests.bukkit.hook.papi;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.entity.Player;

public interface AbstractPlaceholderAPIHook {

    String replacePlaceholders(Player player, String text);

    void registerExpansion(BukkitQuestsPlugin plugin);

    void unregisterExpansion();

}
