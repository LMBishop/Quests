package com.leonardobishop.quests.bukkit.tasktype.type.dependent.citizens.interact;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcInteractTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcInteractTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class CitizensInteractTaskType extends NpcInteractTaskType<Integer> {

    private final BukkitQuestsPlugin plugin;

    public CitizensInteractTaskType(BukkitQuestsPlugin plugin) {
        super("citizens_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with a Citizens NPC to complete the quest.", NpcInteractTask.INTEGER_ID_SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        handle(event.getClicker(), npc.getId(), npc.getName(), plugin);
    }
}
