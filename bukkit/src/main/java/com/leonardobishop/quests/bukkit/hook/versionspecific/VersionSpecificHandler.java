package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;

//TODO move titles, itemgetter, other version specific shite in here
public interface VersionSpecificHandler {

    int getMinecraftVersion();

    boolean isPlayerGliding(Player player);

    boolean isPlayerOnStrider(Player player);

}
