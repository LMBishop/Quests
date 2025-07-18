package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.bukkit.util.CompatUtils;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class VersionSpecificHandler21 extends VersionSpecificHandler20 implements VersionSpecificHandler {

    // Introduced in 1.21.6
    private static final Predicate<Player> HAPPY_GHAST_PREDICATE;

    static {
        if (CompatUtils.classExists("org.bukkit.entity.HappyGhast")) {
            HAPPY_GHAST_PREDICATE = player -> player.getVehicle() instanceof HappyGhast;
        } else {
            HAPPY_GHAST_PREDICATE = player -> Boolean.FALSE;
        }
    }

    @Override
    public int getMinecraftVersion() {
        return 21;
    }

    @Override
    public boolean isPlayerOnHappyGhast(Player player) {
        return HAPPY_GHAST_PREDICATE.test(player);
    }
}
