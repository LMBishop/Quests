package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.jetbrains.annotations.NotNull;

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
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Mode.STRING_MODE_MAP.keySet(), "mode"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getVehicle() instanceof RideableMinecart) {
            return; // minecarts movement is already handled by VehicleMoveEvent
        }

        handle(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        List<Entity> entities = event.getVehicle().getPassengers();
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                handle(player);
            }
        }
    }

    private void handle(Player player) {
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

            // not suspicious at all ඞ
            //noinspection SuspiciousMethodCalls
            Mode mode = Mode.STRING_MODE_MAP.get(modeObject);

            if (mode != null && !validateMode(player, mode)) {
                super.debug("Player mode does not match required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int distanceNeeded = (int) task.getConfigValue("distance");

            if (progress >= distanceNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, distanceNeeded);
        }
    }

    private boolean validateMode(final @NotNull Player player, final @NotNull Mode mode) {
        return switch (mode) {
            // Vehicles
            case BOAT -> player.getVehicle() instanceof Boat;
            case CAMEL -> this.plugin.getVersionSpecificHandler().isPlayerOnCamel(player);
            case DONKEY -> this.plugin.getVersionSpecificHandler().isPlayerOnDonkey(player);
            case HORSE -> this.plugin.getVersionSpecificHandler().isPlayerOnHorse(player);
            case LLAMA -> this.plugin.getVersionSpecificHandler().isPlayerOnLlama(player);
            case MINECART -> player.getVehicle() instanceof RideableMinecart;
            case MULE -> this.plugin.getVersionSpecificHandler().isPlayerOnMule(player);
            case PIG -> player.getVehicle() instanceof Pig;
            case SKELETON_HORSE -> this.plugin.getVersionSpecificHandler().isPlayerOnSkeletonHorse(player);
            case STRIDER -> this.plugin.getVersionSpecificHandler().isPlayerOnStrider(player);
            case ZOMBIE_HORSE -> this.plugin.getVersionSpecificHandler().isPlayerOnZombieHorse(player);

            // Player movement
            case SNEAKING ->
                // player must be sneaking; cannot be swimming, flying and
                // gliding because sneaking is used to control the height;
                // we ignore sprinting, and it shouldn't affect sneaking
                    player.isSneaking() && !player.isSwimming() && !player.isFlying()
                            && !this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case WALKING ->
                // player cannot be doing anything special as we want the
                // other actions to be counted towards other task modes
                    !player.isSneaking() && !player.isSwimming() && !player.isSprinting() && !player.isFlying()
                            && !this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case RUNNING ->
                // player must be sprinting; cannot be sneaking as it makes
                // running impossible; running and swimming at once is possible,
                // but it's not real running, so we ignore it; we ignore flying
                // as it's definitely not running; running and gliding at once
                // is not possible, so we ignore it as well
                    !player.isSneaking() && !player.isSwimming() && player.isSprinting() && !player.isFlying()
                            && !this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case SWIMMING ->
                // sprinting and sneaking is possible with swimming at once,
                // so we ignore it but not gliding as it's a bit different
                    player.isSwimming()
                            && !this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case FLYING ->
                // sprinting and sneaking is possible with flying at once,
                // so we ignore it but not gliding as it's a bit different
                    player.isFlying()
                            && !this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case ELYTRA ->
                // we can safely ignore any other actions here as there is
                // really no better way to detect flying with elytra
                    this.plugin.getVersionSpecificHandler().isPlayerGliding(player);
        };
    }

    private enum Mode {
        // Vehicles
        BOAT,
        CAMEL,
        DONKEY,
        HORSE,
        LLAMA,
        MINECART,
        MULE,
        PIG,
        SKELETON_HORSE,
        STRIDER,
        ZOMBIE_HORSE,

        // Player movement
        SNEAKING,
        WALKING,
        RUNNING,
        SWIMMING,
        FLYING,
        ELYTRA;

        private static final Map<String, Mode> STRING_MODE_MAP = new HashMap<>() {{
            for (final Mode mode : Mode.values()) {
                this.put(mode.name().toLowerCase(Locale.ROOT), mode);
            }
        }};
    }
}
