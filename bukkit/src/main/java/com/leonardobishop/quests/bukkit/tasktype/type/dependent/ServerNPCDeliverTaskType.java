package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.isnakebuzz.npcapi.entities.SnakeHologram;
import com.isnakebuzz.npcapi.entities.SnakeNPC;
import com.isnakebuzz.npcapi.enums.ClickType;
import com.isnakebuzz.npcapi.events.NPCInteractEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class ServerNPCDeliverTaskType extends DeliverTaskType<String> {

    private final BukkitQuestsPlugin plugin;

    public ServerNPCDeliverTaskType(BukkitQuestsPlugin plugin) {
        super("servernpc_deliver", TaskUtils.TASK_ATTRIBUTION_STRING, "Deliver a set of items to a ServerNPC NPC.");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NPCInteractEvent event) {
        if (event.getClickType() != ClickType.RIGHT_CLICK) {
            return;
        }

        SnakeNPC npc = event.getSnakeNPC();
        SnakeHologram hologram = npc.getHologram();
        String name = String.join("\n", hologram.getLines());

        checkInventory(event.getPlayer(), npc.getName(), name, 1L, plugin);
    }

    @Override
    public String getNPCId(Task task) {
        return (String) task.getConfigValue("npc-id");
    }
}
