package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class WalkingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public WalkingTaskType(BukkitQuestsPlugin plugin) {
        super("walking", TaskUtils.TASK_ATTRIBUTION_STRING, "Walk a set distance.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "distance"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "distance"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Mode.STRING_MODE_MAP.keySet(), "mode", "mode", false));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        int distance = Math.abs(to.getBlockX() - from.getBlockX()) + Math.abs(to.getBlockZ() - from.getBlockZ());
        if (distance == 0) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getVehicle() instanceof RideableMinecart) {
            return; // minecarts movement is already handled by VehicleMoveEvent
        }

        handle(player, distance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        int distance = Math.abs(to.getBlockX() - from.getBlockX()) + Math.abs(to.getBlockZ() - from.getBlockZ());
        if (distance == 0) {
            return;
        }

        List<Entity> passengers = this.plugin.getVersionSpecificHandler().getPassengers(event.getVehicle());
        for (Entity passenger : passengers) {
            if (passenger instanceof Player player) {
                handle(player, distance);
            }
        }
    }

    private void handle(Player player, int distance) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            Object modeObject = task.getConfigValue("mode");
            EnumSet<Mode> modeSet = EnumSet.noneOf(Mode.class);

            if (modeObject instanceof List<?> modeList) {
                for (Object modeListElement : modeList) {
                    //noinspection SuspiciousMethodCalls
                    Mode mode = Mode.STRING_MODE_MAP.get(modeListElement);

                    if (mode != null) {
                        modeSet.add(mode);
                    }
                }
            } else {
                //noinspection SuspiciousMethodCalls
                Mode mode = Mode.STRING_MODE_MAP.get(modeObject);

                if (mode != null) {
                    modeSet.add(mode);
                }
            }

            MODE_CHECK:
            if (!modeSet.isEmpty()) {
                for (Mode mode : modeSet) {
                    if (mode.validate(this.plugin, player)) {
                        break MODE_CHECK;
                    }
                }

                super.debug("Player mode does not match required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress, distance);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int distanceNeeded = (int) task.getConfigValue("distance");

            if (progress >= distanceNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, distanceNeeded);
        }
    }

    private enum Mode {
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

        private static final Map<String, Mode> STRING_MODE_MAP = new HashMap<>() {{
            for (final Mode mode : Mode.values()) {
                this.put(mode.name().toLowerCase(Locale.ROOT), mode);
            }
        }};
    }
}
