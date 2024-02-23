package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public final class FabledSkyBlockLevelTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private Method getIslandMethod;
    private Method getOwnerUUIDMethod;
    private Method getCoopPlayersMethod;
    private Method getIslandLevelMethod;
    private Method getLevelMethod;

    @SuppressWarnings("unchecked")
    public FabledSkyBlockLevelTaskType(BukkitQuestsPlugin plugin) {
        super("fabledskyblock_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level for FabledSkyBlock.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "level"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "level"));

        Class<? extends Event> eventClass = (Class<? extends Event>) CompatUtils.getFirstClassAvailable(
                "com.craftaro.skyblock.api.event.island.IslandLevelChangeEvent", // FabledSkyBlock 3
                "com.songoda.skyblock.api.event.island.IslandLevelChangeEvent" // FabledSkyBlock 2
        );

        if (eventClass == null) {
            plugin.getLogger().severe("Failed to register event handler for FabledSkyBlock task type!");
            plugin.getLogger().severe("FabledSkyBlock version detected: " + CompatUtils.getPluginVersion("FabledSkyBlock"));
            return;
        }

        try {
            getIslandMethod = eventClass.getDeclaredMethod("getIsland");
            getOwnerUUIDMethod = getIslandMethod.getReturnType().getDeclaredMethod("getOwnerUUID");
            getCoopPlayersMethod = getIslandMethod.getReturnType().getDeclaredMethod("getCoopPlayers");
            getIslandLevelMethod = eventClass.getDeclaredMethod("getLevel");
            getLevelMethod = getIslandLevelMethod.getReturnType().getDeclaredMethod("getLevel");
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("Failed to register event handler for FabledSkyBlock task type!");
            plugin.getLogger().severe("FabledSkyBlock version detected: " + CompatUtils.getPluginVersion("FabledSkyBlock"));
            return;
        }

        Method handleMethod;
        try {
            handleMethod = getClass().getDeclaredMethod("handle", Object.class);
        } catch (NoSuchMethodException ignored) {
            return;
        }

        plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, EventExecutor.create(handleMethod, eventClass), plugin, true);
    }

    @SuppressWarnings("unchecked")
    private void handle(Object event) {
        ArrayList<UUID> members;
        long level;

        try {
            Object island = getIslandMethod.invoke(event);
            UUID ownerUUID = (UUID) getOwnerUUIDMethod.invoke(island);
            Set<UUID> coopPlayers = (Set<UUID>) getCoopPlayersMethod.invoke(island);

            members = new ArrayList<>(coopPlayers);
            members.add(ownerUUID);

            Object islandLevel = getIslandLevelMethod.invoke(event);
            level = (long) getLevelMethod.invoke(islandLevel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return;
        }

        for (UUID member : members) {
            Player player = plugin.getServer().getPlayer(member);
            if (player == null) {
                continue;
            }

            handle(player, level);
        }
    }

    private void handle(Player player, long level) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug("Player island level updated to " + level, quest.getId(), task.getId(), player.getUniqueId());

            taskProgress.setProgress(level);
            super.debug("Updating task progress (now " + level + ")", quest.getId(), task.getId(), player.getUniqueId());

            int islandLevelNeeded = (int) task.getConfigValue("level");
            if (level >= islandLevelNeeded) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), player.getUniqueId());
                taskProgress.setProgress(islandLevelNeeded);
                taskProgress.setCompleted(true);
            }

            TaskUtils.sendTrackAdvancement(player, quest, task, pendingTask, islandLevelNeeded);
        }
    }
}
