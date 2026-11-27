package com.leonardobishop.quests.bukkit.tasktype.type.dependent.fancynpcs.interact;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcInteractTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcInteractTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class FancyNpcsInteractTaskType extends NpcInteractTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public FancyNpcsInteractTaskType(BukkitQuestsPlugin plugin) {
        super("fancynpcs_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with a FancyNpcs NPC to complete the quest.", NpcInteractTask.STRING_ID_SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNpcInteract(NpcInteractEvent event) {
        ActionTrigger trigger = event.getInteractionType();

        if (trigger != ActionTrigger.RIGHT_CLICK) {
            return;
        }

        Npc npc = event.getNpc();
        handle(event.getPlayer(), npc.getData().getId(), npc.getData().getDisplayName(), plugin);
    }
}
