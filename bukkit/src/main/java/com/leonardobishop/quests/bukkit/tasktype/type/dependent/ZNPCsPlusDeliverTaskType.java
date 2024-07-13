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

import java.util.List;

public final class ZNPCsPlusDeliverTaskType extends DeliverTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public ZNPCsPlusDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("znpcsplus_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a ZNPCsPlus NPC.");
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
            boolean empty = true;

            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < lineCount; i++) {
                boolean lastLine = (i == lineCount - 1);

                String line;
                try {
                    line = hologram.getLine(i);
                } catch (Throwable ignored) {
                    if (lastLine) {
                        nameBuilder.deleteCharAt(nameBuilder.length() - 1);
                    }
                    continue;
                }

                nameBuilder.append(line);
                if (empty) empty = false;

                if (!lastLine) {
                    nameBuilder.append('\n');
                }
            }

            name = !empty ? nameBuilder.toString() : null;
        } else {
            name = null;
        }

        checkInventory(event.getPlayer(), entry.getId(), name, 1L, plugin);
    }

    @Override
    public List<String> getNPCId(Task task) {
        return TaskUtils.getConfigStringList(task, "npc-id");
    }
}
