package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEntityEvent;

public final class BucketEntityTaskType extends BucketInteractionTaskType {

    public BucketEntityTaskType(BukkitQuestsPlugin plugin) {
        super(plugin, "bucketentity", TaskUtils.TASK_ATTRIBUTION_STRING, "Capture specific entity in a bucket.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEntityEvent event) {
        handle(event.getPlayer(), event.getEntityBucket());
    }
}
