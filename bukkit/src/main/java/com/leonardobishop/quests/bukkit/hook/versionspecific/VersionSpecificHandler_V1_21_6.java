package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;

public class VersionSpecificHandler_V1_21_6 extends VersionSpecificHandler_V1_20_4 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_21_6;
    }

    @Override
    public boolean isPlayerOnHappyGhast(Player player) {
        return player.getVehicle() instanceof HappyGhast;
    }
}
