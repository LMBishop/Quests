package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.bukkit.util.CompatUtils;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Nautilus;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class VersionSpecificHandler21 extends VersionSpecificHandler20 implements VersionSpecificHandler {

    // Introduced in 1.21.6
    private static final Predicate<Player> HAPPY_GHAST_PREDICATE;

    // Introduced in 1.21.11
    private static final Predicate<Player> NAUTILUS_PREDICATE;

    static {
        if (CompatUtils.classExists("org.bukkit.entity.HappyGhast")) {
            HAPPY_GHAST_PREDICATE = player -> player.getVehicle() instanceof HappyGhast;
        } else {
            HAPPY_GHAST_PREDICATE = player -> Boolean.FALSE;
        }
    }

    static {
        if (CompatUtils.classExists("org.bukkit.entity.Nautilus")) {
            NAUTILUS_PREDICATE = player -> player.getVehicle() instanceof Nautilus;
        } else {
            NAUTILUS_PREDICATE = player -> Boolean.FALSE;
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

    @Override
    public boolean isPlayerOnNautilus(Player player) {
        return NAUTILUS_PREDICATE.test(player);
    }
}
