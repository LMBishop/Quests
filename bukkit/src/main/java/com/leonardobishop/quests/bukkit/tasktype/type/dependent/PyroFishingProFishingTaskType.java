package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PyroFishingProFishingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private Method getPlayerMethod = null;
    private Player lastPlayer = null;

    public PyroFishingProFishingTaskType(BukkitQuestsPlugin plugin) {
        super("pyrofishingpro_fishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of a Pyro fish from the sea.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "fish-number"));

        for (Method method : PyroFishCatchEvent.class.getMethods()) {
            if (method.getReturnType() != Player.class) {
                continue;
            }

            if (method.getParameterTypes().length != 0) {
                continue;
            }

            if (this.getPlayerMethod != null) {
                // set it to null and break as there is more than 1 method returning a player
                this.getPlayerMethod = null;
                break;
            } else {
                this.getPlayerMethod = method;
            }
        }

        if (this.getPlayerMethod == null) {
            this.plugin.getLogger().warning("No valid player getter found for PyroFishCatchEvent, using legacy workaround.");
            this.plugin.getServer().getPluginManager().registerEvents(new PlayerFishListener(), this.plugin);
        }
    }

    private Player getLastPlayer(PyroFishCatchEvent event) {
        if (this.getPlayerMethod != null) {
            try {
                return (Player) this.getPlayerMethod.invoke(event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            return this.lastPlayer;
        }
    }

    private final class PlayerFishListener implements Listener {
        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerFish(PlayerFishEvent event) {
            PlayerFishEvent.State state = event.getState();
            if (state == PlayerFishEvent.State.CAUGHT_FISH) {
                PyroFishingProFishingTaskType.this.lastPlayer = event.getPlayer();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        Player player = getLastPlayer(event);
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        int fishNumber = event.getFishNumber();
        String tier = event.getTier();

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this, TaskConstraintSet.ALL)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player fished item with fish number " + fishNumber + " of tier " + tier, quest.getId(), task.getId(), player.getUniqueId());

            Integer requiredFishNumber = (Integer) task.getConfigValue("fish-number");
            if (requiredFishNumber != null && requiredFishNumber != fishNumber) {
                super.debug("Continuing (fish number does not match the required)...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            String requiredTier = (String) task.getConfigValue("tier");
            if (requiredTier != null && !requiredTier.equals(tier)) {
                super.debug("Continuing (tier does not match the required)...", quest.getId(), task.getId(), player.getUniqueId());
                continue;
            }

            int progress = TaskUtils.incrementIntegerTaskProgress(taskProgress);
            super.debug("Incrementing task progress (now " + progress + ")", quest.getId(), task.getId(), player.getUniqueId());

            int amount = (int) task.getConfigValue("amount");

            if (progress >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, amount);
        }
    }
}
