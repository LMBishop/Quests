package com.leonardobishop.quests.bukkit.tasktype.type.dependent.fancynpcs.deliver;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class FancyNpcsDeliverTaskType extends NpcDeliverTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public FancyNpcsDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("fancynpcs_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a FancyNpcs NPC.", NpcDeliverTask.STRING_ID_SCHEMA);
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
}
