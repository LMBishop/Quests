package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

public class VersionSpecificHandler11 extends VersionSpecificHandler9 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 11;
    }

    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof AbstractHorse;
    }
}
