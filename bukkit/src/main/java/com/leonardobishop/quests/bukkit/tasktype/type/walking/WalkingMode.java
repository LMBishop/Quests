package com.leonardobishop.quests.bukkit.tasktype.type.walking;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum WalkingMode {
    // Vehicles
    BOAT {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return player.getVehicle() instanceof Boat;
        }
    },
    CAMEL {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnCamel(player);
        }
    },
    CAMEL_HUSK {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnCamelHusk(player);
        }
    },
    DONKEY {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnDonkey(player);
        }
    },
    HAPPY_GHAST {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnHappyGhast(player);
        }
    },
    HORSE {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnHorse(player);
        }
    },
    LLAMA {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnLlama(player);
        }
    },
    MINECART {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return player.getVehicle() instanceof RideableMinecart;
        }
    },
    MULE {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnMule(player);
        }
    },
    NAUTILUS {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnNautilus(player);
        }
    },
    PIG {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return player.getVehicle() instanceof Pig;
        }
    },
    SKELETON_HORSE {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnSkeletonHorse(player);
        }
    },
    STRIDER {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnStrider(player);
        }
    },
    ZOMBIE_HORSE {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnZombieHorse(player);
        }
    },
    ZOMBIE_NAUTILUS {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            return plugin.getVersionSpecificHandler().isPlayerOnZombieNautilus(player);
        }
    },

    // Player movement
    SNEAKING {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player must be sneaking; cannot be swimming, flying and
            // gliding because sneaking is used to control the height;
            // we ignore sprinting, and it shouldn't affect sneaking
            return !player.isInsideVehicle() && player.isSneaking() && !player.isSwimming() && !player.isFlying()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    WALKING {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player cannot be doing anything special as we want the
            // other actions to be counted towards other task modes
            return !player.isInsideVehicle() && !player.isSneaking() && !player.isSwimming() && !player.isSprinting()
                    && !player.isFlying() && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    RUNNING {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player must be sprinting; cannot be sneaking as it makes
            // running impossible; running and swimming at once is possible,
            // but it's not real running, so we ignore it; we ignore flying
            // as it's definitely not running; running and gliding at once
            // is not possible, so we ignore it as well
            return !player.isInsideVehicle() && !player.isSneaking() && !player.isSwimming() && player.isSprinting()
                    && !player.isFlying() && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    SWIMMING {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // sprinting and sneaking is possible with swimming at once,
            // so we ignore it but not gliding as it's a bit different
            return !player.isInsideVehicle() && player.isSwimming()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    FLYING {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // sprinting and sneaking is possible with flying at once,
            // so we ignore it but not gliding as it's a bit different
            return !player.isInsideVehicle() && player.isFlying()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    ELYTRA {
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // we can safely ignore any other actions here as there is
            // really no better way to detect flying with elytra
            return !player.isInsideVehicle() && plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },

    // Grouped
    GROUND {
        // walking, running, sneaking
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player is sneaking, walking or running
            return !player.isInsideVehicle() && !player.isSwimming() && !player.isFlying()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    MANUAL_NO_FLIGHT {
        // walking, running, sneaking, swimming
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player is sneaking, walking, running, or swimming
            return !player.isInsideVehicle() && !player.isFlying()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
        }
    },
    MANUAL_NO_SWIM {
        // walking, running, sneaking, flying
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player is sneaking, walking, running, or flying (creative or elytra)
            return !player.isInsideVehicle() && (!player.isSwimming() || player.isFlying()
                    || plugin.getVersionSpecificHandler().isPlayerGliding(player));
        }
    },
    MANUAL {
        // walking, running, sneaking, swimming, flying
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player is sneaking, walking, running, swimming or flying (creative or elytra)
            return !player.isInsideVehicle();
        }
    },
    VEHICLE {
        // any vehicle
        @Override
        public boolean validate(final BukkitQuestsPlugin plugin, final Player player) {
            // player is in any vehicle
            return player.isInsideVehicle();
        }
    };

    public abstract boolean validate(final BukkitQuestsPlugin plugin, final Player player);

    static final Map<String, WalkingMode> STRING_MODE_MAP = new HashMap<>() {{
        for (final WalkingMode mode : WalkingMode.values()) {
            this.put(mode.name().toLowerCase(Locale.ROOT), mode);
        }
    }};
}
