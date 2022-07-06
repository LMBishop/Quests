package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public final class BrewingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final HashMap<Location, UUID> brewingStands = new HashMap<>();

    public BrewingTaskType(BukkitQuestsPlugin plugin) {
        super("brewing", TaskUtils.TASK_ATTRIBUTION_STRING, "Brew a potion.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
                brewingStands.put(event.getClickedBlock().getLocation(), event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BrewEvent event) {
        UUID uuid;
        if ((uuid = brewingStands.get(event.getBlock().getLocation())) != null) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || player.hasMetadata("NPC")) {
                return;
            }

            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                return;
            }

            for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player.getPlayer(), qPlayer, this, TaskUtils.TaskConstraint.WORLD)) {
                Quest quest = pendingTask.quest();
                Task task = pendingTask.task();
                TaskProgress taskProgress = pendingTask.taskProgress();

                super.debug("Player brewed potion", quest.getId(), task.getId(), player.getUniqueId());

                int progress = 0;

                for (int i = 0; i < 3; i++) {
                    if (event.getContents().getItem(i) != null) {
                        progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
                        super.debug("Incrementing task progress for brewed potion in slot " + i + " (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());
                    } else {
                        super.debug("Slot " + i + " does not have a brewed potion", quest.getId(), task.getId(), player.getUniqueId());
                    }
                }

                int potionsNeeded = (int) task.getConfigValue("amount");

                if (progress >= potionsNeeded) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                    taskProgress.setCompleted(true);
                }
            }
        }
    }

}
