package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
                "horse",
                "pig",
                "minecart",
                "strider",
                "sneaking",
                "walking",
                "running",
                "swimming",
                "flying",
                "elytra"
        ), "mode"));
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".distance", config.get("distance"), problems, "distance", super.getType()))
            TaskUtils.configValidateInt(root + ".distance", config.get("distance"), problems, false, true, "distance");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.hasMetadata("NPC")) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player moved", quest.getId(), task.getId(), player.getUniqueId());

            if (task.getConfigValue("mode") != null
                    && !validateTransportMethod(player, task.getConfigValue("mode").toString())) {
                super.debug("Player's mode does not match required mode, continuing...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int distanceNeeded = (int) task.getConfigValue("distance");

            if (progress >= distanceNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }
        }
    }

    private boolean validateTransportMethod(Player player, String mode) {
        return switch (mode.toLowerCase()) {
            case "boat" -> player.getVehicle() != null && player.getVehicle().getType() == EntityType.BOAT;
            case "horse" -> player.getVehicle() != null && player.getVehicle().getType() == EntityType.HORSE;
            case "pig" -> player.getVehicle() != null && player.getVehicle().getType() == EntityType.PIG;
            case "minecart" -> player.getVehicle() != null && player.getVehicle().getType() == EntityType.MINECART;
            case "strider" -> plugin.getVersionSpecificHandler().isPlayerOnStrider(player);
            case "sneaking" -> // sprinting does not matter
                    player.isSneaking() && !player.isSwimming() && !player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "walking" ->
                    !player.isSneaking() && !player.isSwimming() && !player.isSprinting() && !player.isFlying()
                            && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "running" -> !player.isSneaking() && !player.isSwimming() && player.isSprinting() && !player.isFlying()
                    && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "swimming" -> // sprinting and sneaking do not matter, flying is not possible
                    player.isSwimming() && !plugin.getVersionSpecificHandler().isPlayerGliding(player);
            case "flying" -> // if the player is flying then the player is flying
                    player.isFlying();
            case "elytra" -> // if the player is gliding then the player is gliding
                    plugin.getVersionSpecificHandler().isPlayerGliding(player);
            default -> false;
        };
    }

}
