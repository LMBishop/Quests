package com.leonardobishop.quests.bukkit.tasktype.type.dependent.servernpc.deliver;

import com.isnakebuzz.npcapi.entities.SnakeHologram;
import com.isnakebuzz.npcapi.entities.SnakeNPC;
import com.isnakebuzz.npcapi.enums.ClickType;
import com.isnakebuzz.npcapi.events.NPCInteractEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTask;
import com.leonardobishop.quests.bukkit.tasktype.type.dependent.npc.NpcDeliverTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public final class ServerNPCDeliverTaskType extends NpcDeliverTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public ServerNPCDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("servernpc_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a ServerNPC NPC.", NpcDeliverTask.STRING_ID_SCHEMA);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NPCInteractEvent event) {
        if (event.getClickType() != ClickType.RIGHT_CLICK) {
            return;
        }

        SnakeNPC npc = event.getSnakeNPC();
        SnakeHologram hologram = npc.getSettings().getHologram();
        String name;

        if (hologram != null) {
            List<String> hologramLines = hologram.getLines();
            name = !hologramLines.isEmpty()
                    ? String.join("\n", hologramLines)
                    : null;
        } else {
            name = null;
        }

        checkInventory(event.getPlayer(), npc.getDisplayName(), name, 1L, plugin);
    }
}
