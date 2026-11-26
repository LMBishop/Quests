package com.leonardobishop.quests.bukkit.tasktype.type.dependent.citizens.deliver;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class CitizensDeliverTaskType extends NpcDeliverTaskType<Integer> {

    private final BukkitQuestsPlugin plugin;

    public CitizensDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a Citizens NPC.", NpcDeliverTask.INTEGER_ID_SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        checkInventory(event.getClicker(), npc.getId(), npc.getName(), 1L, plugin);
    }
}
