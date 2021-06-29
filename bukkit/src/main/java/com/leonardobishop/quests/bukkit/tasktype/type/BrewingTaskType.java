package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class BrewingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private final HashMap<Location, UUID> brewingStands = new HashMap<>();

    public BrewingTaskType(BukkitQuestsPlugin plugin) {
        super("brewing", TaskUtils.TASK_ATTRIBUTION_STRING, "Brew a potion.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
                brewingStands.put(event.getClickedBlock().getLocation(), event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BrewEvent event) {
        UUID uuid;
        if ((uuid = brewingStands.get(event.getBlock().getLocation())) != null) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || player.hasMetadata("NPC")) {
                return;
            }

            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) {
                return;
            }

            for (Quest quest : super.getRegisteredQuests()) {
                if (qPlayer.hasStartedQuest(quest)) {
                    QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                    for (Task task : quest.getTasksOfType(super.getType())) {
                        if (!TaskUtils.validateWorld(player, task)) continue;

                        TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                        if (taskProgress.isCompleted()) {
                            continue;
                        }

                        int potionsNeeded = (int) task.getConfigValue("amount");

                        int progress;
                        if (taskProgress.getProgress() == null) {
                            progress = 0;
                        } else {
                            progress = (int) taskProgress.getProgress();
                        }

                        ItemStack potion1 = event.getContents().getItem(0);
                        ItemStack potion2 = event.getContents().getItem(1);
                        ItemStack potion3 = event.getContents().getItem(2);

                        taskProgress.setProgress(progress + (potion1 == null ? 0 : 1) + (potion2 == null ? 0 : 1) + (potion3 == null ? 0 : 1));

                        if (((int) taskProgress.getProgress()) >= potionsNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }

}
