package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;

public class VersionSpecificHandler9 extends VersionSpecificHandler8 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 9;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return player.isGliding();
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return false;
    }
}
