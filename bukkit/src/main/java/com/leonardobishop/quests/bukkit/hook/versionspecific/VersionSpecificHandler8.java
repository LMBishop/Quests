package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;

public class VersionSpecificHandler8 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 8;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return false;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return false;
    }
}
