package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.CamelHusk;
import org.bukkit.entity.Nautilus;
import org.bukkit.entity.Player;

public class VersionSpecificHandler_V1_21_11 extends VersionSpecificHandler_V1_21_6 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_21_11;
    }

    @Override
    public boolean isPlayerOnCamelHusk(Player player) {
        return player.getVehicle() instanceof CamelHusk;
    }

    @Override
    public boolean isPlayerOnNautilus(Player player) {
        return player.getVehicle() instanceof Nautilus;
    }
}
