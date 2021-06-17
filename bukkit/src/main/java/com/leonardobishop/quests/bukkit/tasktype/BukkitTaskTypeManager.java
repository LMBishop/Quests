package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;

public class BukkitTaskTypeManager extends TaskTypeManager {

    private final BukkitQuestsPlugin plugin;

    public BukkitTaskTypeManager(BukkitQuestsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerTaskType(TaskType taskType) {
        if (!(taskType instanceof BukkitTaskType)) throw new RuntimeException("task type must be instance of BukkitTaskType!");

        BukkitTaskType bukkitTaskType = (BukkitTaskType) taskType;
        super.registerTaskType(taskType);
        plugin.getServer().getPluginManager().registerEvents(bukkitTaskType, plugin);
    }

}
