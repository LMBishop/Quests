package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.hologram.Hologram;
import lol.pyr.znpcsplus.api.interaction.InteractionType;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class ZNPCsPlusInteractTaskType extends InteractTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public ZNPCsPlusInteractTaskType(BukkitQuestsPlugin plugin) {
        super("znpcsplus_interact", TaskUtils.TASK_ATTRIBUTION_STRING, "Interact with a ZNPCsPlus NPC to complete the quest.");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNpcInteract(NpcInteractEvent event) {
        if (event.getClickType() != InteractionType.RIGHT_CLICK) {
            return;
        }

        NpcEntry entry = event.getEntry();
        Hologram hologram = event.getNpc().getHologram();
        String name;

        int lineCount = hologram.lineCount();
        if (lineCount > 0) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < lineCount; i++) {
                String line = hologram.getLine(i);
                nameBuilder.append(line);

                if (i != lineCount - 1) {
                    nameBuilder.append('\n');
                }
            }
            name = nameBuilder.toString();
        } else {
            name = null;
        }

        handle(event.getPlayer(), entry.getId(), name, plugin);
    }

    @Override
    public String getNPCId(Task task) {
        return (String) task.getConfigValue("npc-id");
    }
}
