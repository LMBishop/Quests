package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public final class CitizensDeliverTaskType extends DeliverTaskType<Integer> {

    private final BukkitQuestsPlugin plugin;

    public CitizensDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a Citizens NPC.");
        this.plugin = plugin;

        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "npc-id"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        checkInventory(event.getClicker(), npc.getId(), npc.getName(), 1L, plugin);
    }

    @Override
    public List<Integer> getNPCId(Task task) {
        return TaskUtils.getConfigIntegerList(task, "npc-id");
    }
}
