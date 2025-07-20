package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.oheers.fish.api.EMFFishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class EvenMoreFishFishingTaskType extends EvenMoreFishFishTaskType {

    public EvenMoreFishFishingTaskType(BukkitQuestsPlugin plugin) {
        super("evenmorefish_fishing", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of EvenMoreFish fish", plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEMFFish(EMFFishEvent event) {
        this.handle(event.getPlayer(), event.getFish());
    }
}
