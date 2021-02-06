package com.leonardobishop.quests.hooks.papi;

import com.leonardobishop.quests.Quests;
import org.bukkit.entity.Player;

public interface IPlaceholderAPIHook {

    String replacePlaceholders(Player player, String text);

    void registerExpansion(Quests plugin);

    void unregisterExpansion();

}
