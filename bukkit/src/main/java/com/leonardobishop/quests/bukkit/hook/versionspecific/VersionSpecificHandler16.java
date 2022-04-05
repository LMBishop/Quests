package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class VersionSpecificHandler16 extends VersionSpecificHandler9 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 16;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return player.getVehicle() != null && player.getVehicle().getType() == EntityType.STRIDER;
    }
}
