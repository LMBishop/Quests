package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public final class FancyNpcsDeliverTaskType extends DeliverTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public FancyNpcsDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("fancynpcs_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a FancyNpcs NPC.");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNpcInteract(NpcInteractEvent event) {
        ActionTrigger trigger = event.getInteractionType();

        if (trigger != ActionTrigger.RIGHT_CLICK) {
            return;
        }

        Npc npc = event.getNpc();
        checkInventory(event.getPlayer(), npc.getData().getId(), npc.getData().getDisplayName(), 1L, plugin);
    }

    @Override
    public List<String> getNPCId(Task task) {
        return TaskUtils.getConfigStringList(task, "npc-id");
    }
}
