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

public final class FancyNpcsInteractTaskType extends InteractTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public FancyNpcsInteractTaskType(BukkitQuestsPlugin plugin) {
        super("fancynpcs_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with a FancyNpcs NPC to complete the quest.");
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

    @Override
    public List<String> getNPCId(Task task) {
        return TaskUtils.getConfigStringList(task, "npc-id");
    }
}
