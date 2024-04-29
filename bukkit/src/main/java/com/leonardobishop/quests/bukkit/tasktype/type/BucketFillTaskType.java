package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;

public final class BucketFillTaskType extends BucketInteractionTaskType {

    public BucketFillTaskType(BukkitQuestsPlugin plugin) {
        super(plugin, "bucketfill", TaskUtils.TASK_ATTRIBUTION_STRING, "Fill a specific bucket.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        handle(event.getPlayer(), event.getItemStack());
    }
}
