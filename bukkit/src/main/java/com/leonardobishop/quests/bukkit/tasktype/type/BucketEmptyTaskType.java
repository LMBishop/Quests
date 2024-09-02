package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public final class BucketEmptyTaskType extends BucketInteractionTaskType {

    public BucketEmptyTaskType(BukkitQuestsPlugin plugin) {
        super(plugin, "bucketempty", TaskUtils.TASK_ATTRIBUTION_STRING, "Empty a specific bucket.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        handle(event.getPlayer(), plugin.getVersionSpecificHandler().getItem(event));
    }
}
