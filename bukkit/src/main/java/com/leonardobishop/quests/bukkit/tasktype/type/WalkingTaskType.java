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

import java.util.Arrays;
import java.util.List;

public final class WalkingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public WalkingTaskType(BukkitQuestsPlugin plugin) {
        super("walking", TaskUtils.TASK_ATTRIBUTION_STRING, "Walk a set distance.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "distance"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "distance"));
        super.addConfigValidator(TaskUtils.useAcceptedValuesConfigValidator(this, Arrays.asList(
                "boat",
                "camel",
                "donkey",
                "horse",
                "llama",
                "minecart",
                "mule",
                "pig",
                "skeleton_horse",
                "strider",
                "zombie_horse",
                "sneaking",
                "walking",
                "running",
                "swimming",
                "flying",
                "elytra"
        ), "mode"));
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

            String mode = (String) task.getConfigValue("mode");
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

    private boolean validateMode(Player player, String mode) {
        return switch (mode) {
            // Vehicles
            case "boat" -> player.getVehicle() instanceof Boat;
            case "camel" -> plugin.getVersionSpecificHandler().isPlayerOnCamel(player);
            case "donkey" -> plugin.getVersionSpecificHandler().isPlayerOnDonkey(player);
            case "horse" -> plugin.getVersionSpecificHandler().isPlayerOnHorse(player);
            case "llama" -> plugin.getVersionSpecificHandler().isPlayerOnLlama(player);
            case "minecart" -> player.getVehicle() instanceof RideableMinecart;
            case "mule" -> plugin.getVersionSpecificHandler().isPlayerOnMule(player);
            case "pig" -> player.getVehicle() instanceof Pig;
            case "skeleton_horse" -> plugin.getVersionSpecificHandler().isPlayerOnSkeletonHorse(player);
            case "strider" -> plugin.getVersionSpecificHandler().isPlayerOnStrider(player);
            case "zombie_horse" -> plugin.getVersionSpecificHandler().isPlayerOnZombieHorse(player);

            // Player movement
            case "sneaking" ->
                // player must be sneaking; cannot be swimming, flying and
                // gliding because sneaking is used to control the height;
                // we ignore sprinting and it shouldn't affect sneaking
                    player.isSneaking() && !player.isSwimming() && !player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "walking" ->
                // player cannot be doing anything special as we want the
                // other actions to be counted towards other task modes
                    !player.isSneaking() && !player.isSwimming() && !player.isSprinting() && !player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "running" ->
                // player must be sprinting; cannot be sneaking as it makes
                // running impossible; running and swimming at once is possible
                // but it's not real running so we ignore it; we ignore flying
                // as it's definitely not running; running and gliding at once
                // is not possible so we ignore it as well
                    !player.isSneaking() && !player.isSwimming() && player.isSprinting() && !player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "swimming" ->
                // sprinting and sneaking is possible with swimming at once
                // so we ignore it but not gliding as it's a bit different
                    player.isSwimming()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "flying" ->
                // sprinting and sneaking is possible with flying at once
                // so we ignore it but not gliding as it's a bit different
                    player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "elytra" ->
                // we can safely ignore any other actions here as there is
                // really no better way to detect flying with elytra
                    plugin.getVersionSpecificHandler().isPlayerGliding(player);
            default -> false;
        };
    }
}
