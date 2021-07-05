package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.jetbrains.annotations.NotNull;

public class BukkitTaskTypeManager extends TaskTypeManager {

    private final BukkitQuestsPlugin plugin;

    public BukkitTaskTypeManager(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerTaskType(@NotNull TaskType taskType) {
        if (!(taskType instanceof BukkitTaskType)) throw new RuntimeException("BukkitTaskTypeManager implementation can only accept instances of BukkitTaskType!");

        BukkitTaskType bukkitTaskType = (BukkitTaskType) taskType;
        super.registerTaskType(taskType);
        plugin.getServer().getPluginManager().registerEvents(bukkitTaskType, plugin);
    }

}
