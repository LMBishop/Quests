package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.oheers.fish.api.EMFFishHuntEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class EvenMoreFishHuntingTaskType extends EvenMoreFishFishTaskType {

    public EvenMoreFishHuntingTaskType(BukkitQuestsPlugin plugin) {
        super("evenmorefish_hunting", TaskUtils.TASK_ATTRIBUTION_STRING, "Hunt a set amount of EvenMoreFish fish", plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEMFFishHunt(EMFFishHuntEvent event) {
        this.handle(event.getPlayer(), event.getFish());
    }
}
